package com.acme.utils;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author ：wk
 * @date ：Created in 2022/10/5 11:28 下午
 * @description：类相关的工具类
 */
public class ClassUtils {

    /**
     * 获取包下所有的Class
     */
    public static List<String> getClasses(String packageName) {
        if (StringUtils.isEmpty(packageName)) {
            return new ArrayList<>();
        }
        List<String> classes = new ArrayList<>();
        //定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            String pkgPath = packageName.replace(".", "/");
            dirs = Thread.currentThread().getContextClassLoader().getResources(pkgPath);
            while (dirs.hasMoreElements()) {
                //获取下一个元素
                URL url = dirs.nextElement();
                //获取包的物理路径
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                //以文件的方式扫描整个包下的文件 并添加到集合中
                findAndClassesInPackageByFile(packageName, filePath, classes);
            }
        } catch (Exception e) {

        }
        return classes;
    }

    /**
     * 以文件的方式扫描整个包下的class文件 并添加到集合中
     * @param packageName 包名
     * @param filePath 包的物理路径
     * @param classes List<Class<?>>集合
     */
    private static void findAndClassesInPackageByFile(String packageName, String filePath, List<String> classes) {
        File file = new File(filePath);
        //如果不存在或者 也不是目录就直接返回
        if (!file.exists() || !file.isDirectory()) {
            return;
        }
        Arrays.stream(Optional.ofNullable(file.listFiles(pathname -> pathname.isDirectory() || pathname.getName().endsWith("class"))).orElse(new File[0])).forEach(f -> {
            //如果是目录，则将packageName + "." 拼接上f.getName()
            String prefix = packageName + ".";
            if (f.isDirectory()) {
                findAndClassesInPackageByFile(prefix + f.getName(), f.getAbsolutePath(), classes);
            } else {
                //如果是java类文件 去掉后面的.class 只留下类名
                classes.add(prefix + f.getName().substring(0, f.getName().length() - 6));
            }
        });
    }
}
