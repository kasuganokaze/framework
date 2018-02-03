package org.kaze.framework.orm;

import org.kaze.framework.core.support.ClassScanner;
import org.kaze.framework.orm.annotation.Column;
import org.kaze.framework.orm.annotation.Entity;
import org.kaze.framework.orm.annotation.Id;
import org.kaze.framework.orm.annotation.Table;
import org.kaze.framework.util.MapUtil;
import org.kaze.framework.util.StringUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 初始化 Entity 结构
 *
 * @author kaze
 * @since 2017/09/03
 */
public final class EntityHelper {

    /**
     * 实体类与表映射
     */
    private static final Map<Class<?>, String> entityMap = new HashMap<>();
    /**
     * 实体类与（字段，列名）映射
     */
    private static final Map<Class<?>, Map<String, String>> entityFieldMap = new HashMap<>();
    /**
     * 实体类与主键映射
     */
    private static final Map<Class<?>, String> tableIdMap = new HashMap<>();
    private static final Map<Class<?>, String> fieldIdMap = new HashMap<>();

    static {
        //获取遍历所有实体类
        Set<Class<?>> entityClassSet = ClassScanner.getClassSetByAnnotation(Entity.class);
        for (Class<?> entityClass : entityClassSet) {
            initEntityMap(entityClass);
            initEntityFieldMap(entityClass);
        }
    }

    private static void initEntityMap(Class<?> entityClass) {
        String tableName;
        //判断是否需有table注解
        if (entityClass.isAnnotationPresent(Table.class)) {
            // 若已存在，则使用该注解中定义的表名
            tableName = entityClass.getAnnotation(Table.class).name();
        } else {
            // 若不存在，则使用类名作为表名
            tableName = entityClass.getSimpleName();
        }
        entityMap.put(entityClass, tableName);
    }

    private static void initEntityFieldMap(Class<?> entityClass) {
        //获取该是实体类中所有字段
        Field[] fields = entityClass.getDeclaredFields();
        Map<String, String> fieldMap = new HashMap<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                fieldMap.put(field.getName(), field.getAnnotation(Column.class).name());
                if (field.isAnnotationPresent(Id.class)) {
                    tableIdMap.put(entityClass, field.getAnnotation(Column.class).name());
                    fieldIdMap.put(entityClass, field.getName());
                }
            }
        }
        entityFieldMap.put(entityClass, fieldMap);
    }

    public static String getTableName(Class<?> entityClass) {
        return entityMap.get(entityClass);
    }

    public static Map<String, String> getFieldMap(Class<?> entityClass) {
        return entityFieldMap.get(entityClass);
    }

    public static Map<String, String> getColumnMap(Class<?> entityClass) {
        return MapUtil.invert(getFieldMap(entityClass));
    }

    public static String getColumnName(Class<?> entityClass, String fieldName) {
        String columnName = getFieldMap(entityClass).get(fieldName);
        return StringUtil.isNotEmpty(columnName) ? columnName : fieldName;
    }

    public static String getTableId(Class<?> entityClass) {
        String id = tableIdMap.get(entityClass);
        return StringUtil.isNotEmpty(id) ? id.toLowerCase() : "id";
    }

    public static String getFieldId(Class<?> entityClass) {
        String id = fieldIdMap.get(entityClass);
        return StringUtil.isNotEmpty(id) ? id : "id";
    }

}
