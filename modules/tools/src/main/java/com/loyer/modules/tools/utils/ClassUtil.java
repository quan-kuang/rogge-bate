package com.loyer.modules.tools.utils;

import com.loyer.common.core.constant.SpecialCharsConst;
import lombok.SneakyThrows;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 获取指定包下所有类名
 *
 * @author kuangq
 * @date 2019-12-12 14:10
 */
public class ClassUtil {

    /**
     * 获取指定包下所有类名
     *
     * @author kuangq
     * @date 2019-12-12 14:48
     */
    @SneakyThrows
    public static Set<String> getClassName(String packageName, boolean isRecursion) {
        Set<String> set = null;
        String packagePath = packageName.replace(SpecialCharsConst.PERIOD, SpecialCharsConst.SLASH);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(packagePath);
        if (url != null) {
            String file = "file";
            String jar = "jar";
            String protocol = url.getProtocol();
            if (file.equals(protocol)) {
                set = getClassNameFromDir(url.getPath(), packageName, isRecursion);
            } else if (jar.equals(protocol)) {
                JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
                if (jarFile != null) {
                    set = getClassNameFromJar(jarFile.entries(), packageName, isRecursion);
                }
            }
        } else {
            set = getClassNameFromJars(((URLClassLoader) classLoader).getURLs(), packageName, isRecursion);
        }
        return set;
    }

    /**
     * 从项目文件下获取所有类
     *
     * @author kuangq
     * @date 2019-12-12 14:53
     */
    private static Set<String> getClassNameFromDir(String filePath, String packageName, boolean isRecursion) {
        Set<String> set = new HashSet<>();
        File[] files = new File(filePath).listFiles();
        for (File file : Objects.requireNonNull(files)) {
            if (file.isDirectory()) {
                if (isRecursion) {
                    set.addAll(getClassNameFromDir(file.getPath(), packageName + "." + file.getName(), isRecursion));
                }
            } else {
                String fileName = file.getName();
                if (fileName.endsWith(".class") && !fileName.contains("$")) {
                    set.add(packageName + "." + fileName.replace(".class", ""));
                }
            }
        }
        return set;
    }

    /**
     * 从jar包中获取所有类
     *
     * @author kuangq
     * @date 2019-12-12 14:53
     */
    private static Set<String> getClassNameFromJar(Enumeration<JarEntry> jarEntries, String packageName, boolean isRecursion) {
        Set<String> set = new HashSet<>();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            if (!jarEntry.isDirectory()) {
                String entryName = jarEntry.getName().replace(SpecialCharsConst.SLASH, SpecialCharsConst.PERIOD);
                if (entryName.endsWith(".class") && !entryName.contains("$") && entryName.startsWith(packageName)) {
                    entryName = entryName.replace(".class", "");
                    if (isRecursion) {
                        set.add(entryName);
                    } else if (!entryName.replace(packageName + SpecialCharsConst.PERIOD, SpecialCharsConst.BLANK).contains(SpecialCharsConst.PERIOD)) {
                        set.add(entryName);
                    }
                }
            }
        }
        return set;
    }

    /**
     * 从Jar包中搜索该包，并获取改包下所有类
     *
     * @author kuangq
     * @date 2019-12-12 14:55
     */
    @SneakyThrows
    private static Set<String> getClassNameFromJars(URL[] urls, String packageName, boolean isRecursion) {
        Set<String> set = new HashSet<>();
        for (URL url : urls) {
            String path = url.getPath();
            if (path.endsWith("classes/")) {
                continue;
            }
            try (JarFile jarFile = new JarFile(path.substring(path.indexOf(SpecialCharsConst.SLASH)))) {
                set.addAll(getClassNameFromJar(jarFile.entries(), packageName, isRecursion));
            }
        }
        return set;
    }
}