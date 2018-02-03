package org.kaze.framework.core.support;

import org.apache.commons.lang3.ArrayUtils;
import org.kaze.framework.core.util.ClassUtil;
import org.kaze.framework.util.StringUtil;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类加载
 *
 * @author kaze
 * @since 2017/09/16
 */
public abstract class ClassLoader {

    /**
     * 获取包下所有类
     */
    public Set<Class<?>> getClassSet(String packages) {
        Set<Class<?>> classSet = new HashSet<>();
        String[] packageArray = packages.split(",");
        if (ArrayUtils.isNotEmpty(packageArray)) {
            for (String packageName : packageArray) {
                getClassSet(classSet, packageName);
            }
        }
        return classSet;
    }

    /**
     * 获取扫描包下的类
     */
    private Set<Class<?>> getClassSet(Set<Class<?>> classSet, String packageName) {
        try {
            Enumeration<URL> urls = ClassUtil.getClassLoader().getResources(packageName.replace(".", "/"));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    if (protocol.equals("file")) {
                        String packagePath = url.getPath().replaceAll("%20", " ");
                        addClass(classSet, packagePath, packageName);
                    } else if (protocol.equals("jar")) {
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        if (jarURLConnection != null) {
                            JarFile jarFile = jarURLConnection.getJarFile();
                            if (jarFile != null) {
                                Enumeration<JarEntry> jarEntries = jarFile.entries();
                                while (jarEntries.hasMoreElements()) {
                                    JarEntry jarEntry = jarEntries.nextElement();
                                    String jarEntryName = jarEntry.getName();
                                    if (jarEntryName.endsWith(".class")) {
                                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(
                                                ".")).replaceAll("/", ".");
                                        doAddClass(classSet, className);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException("get class set failure", e);
        }
        return classSet;
    }

    private void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {
        File[] files = new File(packagePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
            }
        });
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                if (StringUtil.isNotEmpty(packageName)) {
                    className = packageName + "." + className;
                }
                doAddClass(classSet, className);
            } else {
                String subPackagePath = fileName;
                if (StringUtil.isNotEmpty(packagePath)) {
                    subPackagePath = packagePath + "/" + subPackagePath;
                }
                String subPackageName = fileName;
                if (StringUtil.isNotEmpty(packageName)) {
                    subPackageName = packageName + "." + subPackageName;
                }
                addClass(classSet, subPackagePath, subPackageName);
            }
        }
    }

    private void doAddClass(Set<Class<?>> classSet, String className) {
        Class cls = ClassUtil.loadClass(className, false);
        if (checkAddClass(cls)) {
            classSet.add(cls);
        }
    }

    public abstract boolean checkAddClass(Class<?> cls);

}
