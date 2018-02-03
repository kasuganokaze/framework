package org.kaze.framework.ds.impl;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;

/**
 * 默认数据源工厂
 *
 * @author kaze
 * @since 2017/09/03
 */
public class DefaultDatasourceFactory extends AbstractDatasourceFactory {

    private static final DefaultDatasourceFactory defaultDatasourceFactory = new DefaultDatasourceFactory();
    private final DruidDataSource dataSource = new DruidDataSource();

    private DefaultDatasourceFactory() {
    }

    public static DefaultDatasourceFactory getInstance() {
        return defaultDatasourceFactory;
    }

    public DataSource getDatasource() {
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setValidationQuery("select 1 from dual");
        return dataSource;
    }

}
