package org.kaze.framework.util;

/**
 * 转型操作工具类
 *
 * @author kaze
 * @since 2017/08/29
 */
public final class ConvertUtil {

    /**
     * 转为int型
     */
    public static int toInt(Object obj) {
        return toInt(obj, 0);
    }

    /**
     * 转为int型(提供默认值)
     */
    public static int toInt(Object obj, int defaultValue) {
        int intValue = defaultValue;
        if (obj != null) {
            String strValue = toString(obj);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    intValue = Integer.parseInt(strValue);
                } catch (NumberFormatException e) {
                    intValue = defaultValue;
                }
            }
        }
        return intValue;
    }

    /**
     * 转为String型
     */
    public static String toString(Object obj) {
        return toString(obj, "");
    }

    /**
     * 转为String型(提供默认值)
     */
    public static String toString(Object obj, String defaultValue) {
        return obj != null ? String.valueOf(obj) : defaultValue;
    }

    /**
     * 转为long型
     */
    public static long toLong(Object obj) {
        return toLong(obj, 0);
    }

    /**
     * 转为long型(提供默认值)
     */
    public static long toLong(Object obj, long defaultValue) {
        long longValue = defaultValue;
        if (obj != null) {
            String strValue = toString(obj);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    longValue = Long.parseLong(strValue);
                } catch (NumberFormatException e) {
                    longValue = defaultValue;
                }
            }
        }
        return longValue;
    }

    /**
     * 转为boolean型
     */
    public static boolean toBoolean(Object obj) {
        return toBoolean(obj, false);
    }

    /**
     * 转为boolean型(提供默认值)
     */
    public static boolean toBoolean(Object obj, boolean defaultValue) {
        boolean booleanValue = defaultValue;
        if (obj != null) {
            booleanValue = Boolean.parseBoolean(toString(obj));
        }
        return booleanValue;
    }

}
