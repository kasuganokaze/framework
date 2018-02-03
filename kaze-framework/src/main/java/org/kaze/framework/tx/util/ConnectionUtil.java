package org.kaze.framework.tx.util;

import org.kaze.framework.ds.impl.DefaultDatasourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务工具类
 *
 * @author kaze
 * @since 2017/09/04
 */
public class ConnectionUtil {

    /**
     * 定义局部线程变量（每个线程有自己的数据库连接）
     */
    private static final ThreadLocal<Connection> connContainer = new ThreadLocal<>();

    /**
     * 获取数据源
     */
    public static DataSource getDatasource() {
        return DefaultDatasourceFactory.getInstance().getDatasource();
    }

    /**
     * 获取连接
     */
    public static Connection getConnection() {
        Connection conn;
        try {
            conn = connContainer.get();
            if (conn == null) {
                conn = getDatasource().getConnection();
                if (conn != null) {
                    connContainer.set(conn);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("get connection failure", e);
        }
        return conn;
    }

    /**
     * 开启事务
     */
    public static void beginTransaction() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                throw new RuntimeException("begin transaction failure", e);
            } finally {
                connContainer.set(conn);
            }
        }
    }

    /**
     * 提交事务
     */
    public static void commitTransaction() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException e) {
                throw new RuntimeException("commit transaction failure", e);
            } finally {
                connContainer.remove();
            }
        }
    }

    /**
     * 回滚事务
     */
    public static void rollbackTransaction() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                throw new RuntimeException("rollback transaction failure", e);
            } finally {
                connContainer.remove();
            }
        }
    }

}
