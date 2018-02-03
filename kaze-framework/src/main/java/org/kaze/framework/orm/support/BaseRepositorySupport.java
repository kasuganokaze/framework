package org.kaze.framework.orm.support;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;
import org.kaze.framework.orm.EntityHelper;
import org.kaze.framework.orm.bean.Page;
import org.kaze.framework.tx.util.ConnectionUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据库操作基类
 *
 * @author kaze
 * @since 2017/09/03
 */
abstract class BaseRepositorySupport<T> {

    private final QueryRunner queryRunner = new QueryRunner(ConnectionUtil.getDatasource());
    final Class<T> cls;

    BaseRepositorySupport() {
        try {
            Type genType = getClass().getGenericSuperclass();
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            cls = (Class<T>) params[0];
        } catch (Exception e) {
            throw new RuntimeException("DAO层未加泛型", e);
        }
    }

    protected T select(String sql, Object... params) {
        T result;
        try {
            Map<String, String> columnMap = EntityHelper.getColumnMap(cls);
            if (MapUtils.isNotEmpty(columnMap)) {
                result = queryRunner.query(sql, new BeanHandler<>(cls, new BasicRowProcessor(new BeanProcessor(columnMap))), params);
            } else {
                result = queryRunner.query(sql, new BeanHandler<>(cls), params);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    protected List<T> selectList(String sql, Object... params) {
        List<T> result;
        try {
            Map<String, String> columnMap = EntityHelper.getColumnMap(cls);
            if (MapUtils.isNotEmpty(columnMap)) {
                result = queryRunner.query(sql, new BeanListHandler<>(cls, new BasicRowProcessor(new BeanProcessor(columnMap))), params);
            } else {
                result = queryRunner.query(sql, new BeanListHandler<>(cls), params);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    protected Map<String, Object> selectMap(String sql, Object... params) {
        Map<String, Object> map;
        try {
            map = queryRunner.query(sql, new MapHandler(), params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    protected List<Map<String, Object>> selectMapList(String sql, Object... params) {
        List<Map<String, Object>> fieldMapList;
        try {
            fieldMapList = queryRunner.query(sql, new MapListHandler(), params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return fieldMapList;
    }

    protected int selectCount(String sql, Object... params) {
        int result;
        try {
            Pattern pattern = Pattern.compile("count\\(.+\\)");
            Matcher matcher = pattern.matcher(sql);
            String countStr = "count(*)";
            if (matcher.find()) {
                countStr = matcher.group();
            }
            result = queryRunner.query(sql, new ScalarHandler<Long>(countStr), params).intValue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    protected int update(String sql, Object... params) {
        int result;
        try {
            Connection conn = ConnectionUtil.getConnection();
            result = queryRunner.update(conn, sql, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 翻页返回实体
     */
    protected Page queryForPage(String sql, Integer pageNum, Integer rows, Object... params) {
        if (pageNum < 1)
            pageNum = 1;
        String countSql = "select count(1) " + sql.substring(sql.toLowerCase().indexOf("from"));
        Integer total = this.selectCount(countSql, params);
        sql += " limit " + (pageNum - 1) * rows + ", " + rows;
        List<T> list = this.selectList(sql, params);
        return new Page(pageNum, rows, total, list);
    }

    /**
     * 翻页返回Map
     */
    protected Page queryMapForPage(String sql, Integer pageNum, Integer rows, Object... params) {
        if (pageNum < 1)
            pageNum = 1;
        String countSql = "select count(1) " + sql.substring(sql.toLowerCase().indexOf("from"));
        Integer total = this.selectCount(countSql, params);
        sql += " limit " + (pageNum - 1) * rows + ", " + rows;
        List<Map<String, Object>> list = this.selectMapList(sql, params);
        return new Page(pageNum, rows, total, list);
    }

}
