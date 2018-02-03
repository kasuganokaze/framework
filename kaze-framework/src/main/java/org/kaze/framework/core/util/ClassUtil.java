package org.kaze.framework.core.util;

/**
 * 类操作工具类
 *
 * @author kaze
 * @since 2017/08/30
 */
public final class ClassUtil {

    /**
     * 获取类加载器
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 加载类（将自动初始化）
     */
    public static Class<?> loadClass(String className) {
        return loadClass(className, true);
    }

    /**
     * 加载类
     */
    public static Class<?> loadClass(String className, boolean isInitialized) {
        Class<?> cls;
        try {
            cls = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("load class failure", e);
        }
        return cls;
    }

    /**
     * 是否为 int 类型（包括 Integer 类型）
     */
    public static boolean isInt(Class<?> type) {
        return type.equals(int.class) || type.equals(Integer.class);
    }

    /**
     * 是否为 int[] 类型（包括 Integer 类型）
     */
    public static boolean isIntArray(Class<?> type) {
        return type.equals(int[].class) || type.equals(Integer[].class);
    }

    /**
     * 是否为 long 类型（包括 Long 类型）
     */
    public static boolean isLong(Class<?> type) {
        return type.equals(long.class) || type.equals(Long.class);
    }

    /**
     * 是否为 long[] 类型（包括 Long 类型）
     */
    public static boolean isLongArray(Class<?> type) {
        return type.equals(long[].class) || type.equals(Long[].class);
    }

    /**
     * 是否为 boolean 类型（包括 Boolean 类型）
     */
    public static boolean isBoolean(Class<?> type) {
        return type.equals(boolean.class) || type.equals(Boolean.class);
    }

    /**
     * 是否为 String 类型
     */
    public static boolean isString(Class<?> type) {
        return type.equals(String.class);
    }

}
