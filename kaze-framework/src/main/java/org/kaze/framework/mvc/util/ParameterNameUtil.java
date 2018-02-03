package org.kaze.framework.mvc.util;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.lang.reflect.Method;

/**
 * 获取方法参数工具类
 *
 * @author kaze
 * @since 2017/09/02
 */
public class ParameterNameUtil {

    /**
     * 获取方法的参数名
     */
    public static String[] getMethodParamNames(final Class<?> clazz, final Method m) {
        String[] paramNames = null;
        ClassPool pool = ClassPool.getDefault();
        // 没有这句会找不到类
        pool.insertClassPath(new ClassClassPath(clazz));
        try {
            CtClass ctClass = pool.get(clazz.getName());
            CtMethod ctMethod = ctClass.getDeclaredMethod(m.getName());
            // 使用javassist的反射方法的参数名
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
                    .getAttribute(LocalVariableAttribute.tag);
            if (attr != null) {
                int len = ctMethod.getParameterTypes().length;
                paramNames = new String[len];
                // 非静态的成员函数的第一个参数是this,如果有try catch（Exception e）代码块，则所有异常参数在最前面
                // 统计参数总共多少，倒着赋值
                int pos = 0;
                while (true) {
                    try {
                        attr.variableName(pos);
                        pos++;
                    } catch (Exception e) {
                        pos = pos - len;
                        break;
                    }
                }
                for (int i = 0; i < len; i++) {
                    paramNames[i] = attr.variableName(i + pos);
                }
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return paramNames;
    }

}
