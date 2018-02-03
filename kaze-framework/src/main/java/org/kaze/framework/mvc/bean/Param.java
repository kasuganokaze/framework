package org.kaze.framework.mvc.bean;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.kaze.framework.core.util.ClassUtil;
import org.kaze.framework.mvc.ControllerHelper;
import org.kaze.framework.mvc.annotation.PathVariable;
import org.kaze.framework.mvc.annotation.RequestParam;
import org.kaze.framework.mvc.util.ParameterNameUtil;
import org.kaze.framework.util.ConvertUtil;
import org.kaze.framework.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.*;

/**
 * 请求参数对象
 *
 * @author kaze
 * @since 2017.9.1
 */
public class Param {

    private List<Object[]> formParamList = new ArrayList<>();
    private Map<String, Object> fieldMap = new HashMap<>();
    private Object[] values;

    public Param(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, Method method) throws Exception {
        parseParameterNames(request);
        parseInputStream(request);
        initFieldMap();
        initValues(request, response, clazz, method);
    }

    /**
     * 从URL或者前台传过来的参数
     */
    private void parseParameterNames(HttpServletRequest request) {
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String fieldName = paramNames.nextElement();
            String[] fieldValues = request.getParameterValues(fieldName);
            if (ArrayUtils.isNotEmpty(fieldValues)) {
                Object fieldValue;
                if (fieldValues.length == 1) {
                    fieldValue = fieldValues[0];
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < fieldValues.length; i++) {
                        sb.append(fieldValues[i]);
                        if (i != fieldValues.length - 1) {
                            sb.append(StringUtil.SEPARATOR);
                        }
                    }
                    fieldValue = sb.toString();
                }
                formParamList.add(new Object[]{fieldName, fieldValue});
            }
        }
    }

    /**
     * 获取body数据
     */
    private void parseInputStream(HttpServletRequest request) {
        try {
            String body = URLDecoder.decode(StringUtil.getString(request.getInputStream()), "UTF-8");
            if (StringUtil.isNotEmpty(body)) {
                String[] kvs = body.split("&");
                if (ArrayUtils.isNotEmpty(kvs)) {
                    for (String kv : kvs) {
                        String[] array = kv.split("=");
                        if (ArrayUtils.isNotEmpty(array) && array.length == 2) {
                            formParamList.add(new Object[]{array[0], array[1]});
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取请求参数映射
     */
    private void initFieldMap() {
        if (CollectionUtils.isNotEmpty(formParamList)) {
            for (Object[] objects : formParamList) {
                String fieldName = objects[0].toString();
                Object fieldValue = objects[1];
                if (fieldMap.containsKey(fieldName)) {
                    fieldValue = fieldMap.get(fieldName) + StringUtil.SEPARATOR + fieldValue;
                }
                fieldMap.put(fieldName, fieldValue);
            }
        }
    }

    /**
     * 获取方法参数
     */
    private void initValues(HttpServletRequest request, HttpServletResponse response, Class<?> clazz, Method method) throws Exception {
        Class<?>[] types = method.getParameterTypes();
        String[] paramNames = ParameterNameUtil.getMethodParamNames(clazz, method);
        values = new Object[types.length];
        Annotation parameterAnnotations[][] = method.getParameterAnnotations();
        Map<String, String> urlMappingMap = ControllerHelper.getUrlMappingMap();
        for (int i = 0; i < types.length; i++) {
            values[i] = fieldMap.get(paramNames[i]);
            if (types[i].equals(HttpServletRequest.class)) {
                values[i] = request;
            } else if (types[i].equals(HttpServletResponse.class)) {
                values[i] = response;
            } else if (types[i].isArray()) { //数组
                if (values[i] == null || "".equals(values[i])) {
                    if (ClassUtil.isIntArray(types[i])) {
                        values[i] = new Integer[0];
                    } else if (ClassUtil.isLongArray(types[i])) {
                        values[i] = new Long[0];
                    } else {
                        values[i] = new Object[0];
                    }
                } else {
                    Object[] objects = values[i].toString().split(StringUtil.SEPARATOR);
                    if (ClassUtil.isIntArray(types[i])) {
                        Integer[] integers = new Integer[objects.length];
                        for (int j = 0; j < integers.length; j++) {
                            integers[j] = ConvertUtil.toInt(objects[j]);
                        }
                        values[i] = integers;
                    } else if (ClassUtil.isLongArray(types[i])) {
                        Long[] longs = new Long[objects.length];
                        for (int j = 0; j < longs.length; j++) {
                            longs[j] = ConvertUtil.toLong(objects[j]);
                        }
                        values[i] = longs;
                    } else {
                        values[i] = objects;
                    }
                }
            } else { //对象
                //如果有PathVariable注解，则优先注解的值,而RequestParam则是没值的时候给默认值
                for (Annotation annotation : parameterAnnotations[i]) {
                    if (annotation.annotationType().equals(PathVariable.class)) {
                        values[i] = urlMappingMap.get("{" + ((PathVariable) annotation).value() + "}");
                    } else if (annotation.annotationType().equals(RequestParam.class) && values[i] == null) {
                        values[i] = ((RequestParam) annotation).defaultValue();
                    }
                }
                if (ClassUtil.isInt(types[i])) {
                    values[i] = ConvertUtil.toInt(values[i]);
                } else if (ClassUtil.isLong(types[i])) {
                    values[i] = ConvertUtil.toLong(values[i]);
                } else if (ClassUtil.isString(types[i]) || types[i].equals(Object.class)) {

                } else {
                    Object object = types[i].newInstance();
                    Field[] fields = types[i].getDeclaredFields();
                    for (Field field : fields) {
                        if (field.getName().equals("serialVersionUID")) {
                            continue;
                        }
                        field.setAccessible(true);
                        String value = (String) fieldMap.get(field.getName());
                        if (ClassUtil.isInt(field.getType())) {
                            field.set(object, ConvertUtil.toInt(value));
                        } else if (ClassUtil.isLong(field.getType())) {
                            field.set(object, ConvertUtil.toLong(value));
                        } else if (ClassUtil.isString(field.getType())) {
                            field.set(object, ConvertUtil.toString(value));
                        }
                    }
                    values[i] = object;
                }
            }
        }
    }

    public Object[] getValues() {
        return values;
    }

}
