package org.kaze.framework.orm.support;

import org.apache.commons.lang3.ArrayUtils;
import org.kaze.framework.orm.EntityHelper;
import org.kaze.framework.orm.util.SqlUtil;
import org.kaze.framework.util.MapUtil;
import org.kaze.framework.util.ObjectUtil;

import java.io.Serializable;
import java.util.Map;

/**
 * 数据库实体操作
 *
 * @author kaze
 * @since 2017/09/03
 */
public abstract class RepositorySupport<T> extends BaseRepositorySupport<T> {

    /**
     * 根据主键查询实体
     */
    public T get(Serializable id) {
        String sql = SqlUtil.generateSelectSql(cls, EntityHelper.getTableId(cls) + "= ?");
        T result = select(sql, id);
        return result;
    }

    /**
     * 插入一个实体
     */
    public int insert(Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException();
        }
        Class<?> entityClass = entity.getClass();
        Map<String, Object> fieldMap = ObjectUtil.getFieldMap(entity);
        if (MapUtil.isEmpty(fieldMap)) {
            return 0;
        }
        String sql = SqlUtil.generateInsertSql(entityClass, fieldMap.keySet());
        int rows = update(sql, fieldMap.values().toArray());
        return rows;
    }

    /**
     * 更新一个实体
     */
    public int update(Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException();
        }
        Class<?> entityClass = entity.getClass();
        Map<String, Object> fieldMap = ObjectUtil.getFieldMap(entity);
        String condition = EntityHelper.getTableId(entityClass) + " = ?";
        Object[] params = {ObjectUtil.getFieldValue(entity, EntityHelper.getFieldId(entityClass))};
        if (MapUtil.isEmpty(fieldMap)) {
            return 0;
        }
        String sql = SqlUtil.generateUpdateSql(entityClass, fieldMap, condition);
        int rows = update(sql, ArrayUtils.addAll(fieldMap.values().toArray(), params));
        return rows;
    }

    /**
     * 删除一个实体
     */
    public int delete(Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException();
        }
        Class<?> entityClass = entity.getClass();
        String condition = EntityHelper.getTableId(entityClass) + " = ?";
        Object[] params = {ObjectUtil.getFieldValue(entity, EntityHelper.getFieldId(entityClass))};
        String sql = SqlUtil.generateDeleteSql(entityClass, condition);
        int rows = update(sql, params);
        return rows;
    }

    /**
     * 根据主键删除实体
     */
    public int delete(Serializable id) {
        String sql = SqlUtil.generateDeleteSql(cls, EntityHelper.getTableId(cls) + "= ?");
        int rows = update(sql, id);
        return rows;
    }

    /**
     * 根据主键删除实体
     */
    public int deleteByIds(Object[] ids) {
        String str = "";
        for (Object id : ids) {
            str += id + ",";
        }
        str = str.substring(0, str.length() - 1);
        String sql = SqlUtil.generateDeleteSql(cls, EntityHelper.getTableId(cls) + " in (" + str + ")");
        int rows = super.update(sql);
        return rows;
    }

}
