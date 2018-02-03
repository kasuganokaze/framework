package org.kaze.framework.core.util;

import org.kaze.framework.util.ConvertUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 属性文件工具类
 *
 * @author kaze
 * @since 2017/08/29
 */
public final class ConfigUtil {

    private static Properties properties = new Properties();

    static {
        String fileName = "kaze.properties";
        InputStream is = null;
        try {
            is = ClassUtil.getClassLoader().getResourceAsStream(fileName);
            if (is == null) {
                throw new FileNotFoundException(fileName + " is not found");
            }
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException("load properties file failure", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException("close input stream failure", e);
                }
            }
        }
    }

    /**
     * 获取应用JSP路径
     */
    public static String getViewPrefix() {
        return getString("kaze.framework.view.prefix", "/WEB-INF/jsp/");
    }

    /**
     * 获取应用JSP后缀
     */
    public static String getViewSuffix() {
        return getString("kaze.framework.view.suffix", ".jsp");
    }

    /**
     * 获取应用静态资源路径
     */
    public static String getStaticPath() {
        return getString("kaze.framework.static.path", "/static/");
    }

    public static String getBasePackage() {
        return getString("kaze.framework.base.package");
    }

    /**
     * 获取字符串属性(默认值为空字符串)
     */
    public static String getString(String key) {
        return getString(key, "");
    }

    /**
     * 获取字符型属性(可指定默认值)
     */
    public static String getString(String key, String defaultValue) {
        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        }
        return defaultValue;
    }

    /**
     * 获取数值型属性(默认值为0)
     */
    public static int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * 获取数值型属性(可指定默认值)
     */
    public static int getInt(String key, int defaultValue) {
        if (properties.containsKey(key)) {
            return ConvertUtil.toInt(properties.getProperty(key));
        }
        return defaultValue;
    }

    /**
     * 获取布尔型属性(默认值为false)
     */
    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * 获取布尔型属性(可指定默认值)
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        if (properties.containsKey(key)) {
            return ConvertUtil.toBoolean(properties.getProperty(key));
        }
        return defaultValue;
    }

}
