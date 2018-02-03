package org.kaze.framework.ds;


import javax.sql.DataSource;

/**
 * 数据源工厂
 *
 * @author kaze
 * @since 2017/09/03
 */
public interface DataSourceFactory {

    DataSource getDatasource();

}
