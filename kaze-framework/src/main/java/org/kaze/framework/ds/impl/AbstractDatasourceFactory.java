package org.kaze.framework.ds.impl;

import org.kaze.framework.core.util.ConfigUtil;
import org.kaze.framework.ds.DataSourceFactory;

import javax.sql.DataSource;

/**
 * 抽象数据源工厂
 *
 * @author kaze
 * @since 2017/09/03
 */
public abstract class AbstractDatasourceFactory implements DataSourceFactory {

    protected final String driver = ConfigUtil.getString("kaze.framework.jdbc.driver");
    protected final String url = ConfigUtil.getString("kaze.framework.jdbc.url");
    protected final String username = ConfigUtil.getString("kaze.framework.jdbc.username");
    protected final String password = ConfigUtil.getString("kaze.framework.jdbc.password");

    public abstract DataSource getDatasource();

}
