package org.kaze.framework.orm.util;

import org.kaze.framework.orm.EntityHelper;
import org.kaze.framework.util.StringUtil;

import java.util.Collection;
import java.util.Map;

/**
 * SQL语句操作
 *
 * @author kaze
 * @since 2017/09/03
 */
public class SqlUtil {

    /**
     * 生成 insert 语句
     */
    public static String generateInsertSql(Class<?> entityClass, Collection<String> fieldNames) {
        StringBuilder sql = new StringBuilder("insert into ").append(EntityHelper.getTableName(entityClass));
        StringBuilder columns = new StringBuilder(" ");
        StringBuilder values = new StringBuilder(" values ");
        int i = 0;
        for (String fieldName : fieldNames) {
            String columnName = EntityHelper.getColumnName(entityClass, fieldName);
            if (i == 0) {
                columns.append("(").append(columnName);
                values.append("(?");
            } else {
                columns.append(", ").append(columnName);
                values.append(", ?");
            }
            if (i == fieldNames.size() - 1) {
                columns.append(")");
                values.append(")");
            }
            i++;
        }
        sql.append(columns).append(values);
        return sql.toString();
    }

    /**
     * 生成 update 语句
     */
    public static String generateUpdateSql(Class<?> entityClass, Map<String, Object> fieldMap, String condition) {
        StringBuilder sql = new StringBuilder("update ").append(EntityHelper.getTableName(entityClass)).append(" set ");
        int i = 0;
        for (Map.Entry<String, Object> fieldEntry : fieldMap.entrySet()) {
            String fieldName = fieldEntry.getKey();
            String columnName = EntityHelper.getColumnName(entityClass, fieldName);
            if (i == 0) {
                sql.append(columnName).append(" = ?");
            } else {
                sql.append(", ").append(columnName).append(" = ?");
            }
            i++;
        }
        sql.append(generateWhere(condition));
        return sql.toString();
    }

    /**
     * 生成 delete 语句
     */
    public static String generateDeleteSql(Class<?> entityClass, String condition) {
        StringBuilder sql = new StringBuilder("delete from ").append(EntityHelper.getTableName(entityClass));
        sql.append(generateWhere(condition));
        return sql.toString();
    }

    /**
     * 生成 select 语句
     */
    public static String generateSelectSql(Class<?> entityClass, String condition) {
        StringBuilder sql = new StringBuilder("select * from ").append(EntityHelper.getTableName(entityClass));
        sql.append(generateWhere(condition));
        return sql.toString();
    }

    private static String generateWhere(String condition) {
        String where = "";
        if (StringUtil.isNotEmpty(condition)) {
            where += " where " + condition;
        }
        return where;
    }

}
