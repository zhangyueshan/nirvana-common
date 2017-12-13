package com.nirvana.common.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by InThEnd on 2016/6/22.
 * Package工具.
 */
public class PackageHelper {

    /*包名*/
    private String packageName;

    /*是否深入子包*/
    private boolean childPackage;

    public PackageHelper(String packageName) {
        this(packageName, true);
    }

    public PackageHelper(String packageName, boolean childPackage) {
        if (packageName == null) {
            throw new IllegalArgumentException("packageName不能为空。");
        }
        this.packageName = packageName;
        this.childPackage = childPackage;
    }

    public List<Class<?>> getClasses() throws Exception {
        List<Class<?>> classList = new ArrayList<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        URL url = loader.getResource(packagePath);
        if (url != null) {
            //如果为file
            if (url.getProtocol().equals("file")) {
                File file;
                try {
                    file = new File(url.toURI());
                    classList = getDirectoryPathClasses(file.getPath(), packageName);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
            //如果为jar包
            else if (url.getProtocol().equals("jar")) {
            }
            //如果透过vfs
            else if (url.getProtocol().equals("vfs")) {
                Object content = url.openConnection().getContent();
                Class<?> czz = Class.forName("org.jboss.vfs.VirtualFile");
                Method m = czz.getMethod("getPhysicalFile");
                File physicalFile = (File) m.invoke(content);
                classList = getDirectoryPathClasses(physicalFile.getPath(), packageName);
            }

        }
        return classList;
    }

    private List<Class<?>> getDirectoryPathClasses(String path, String packageName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        List<Class<?>> classes = new ArrayList<>();
        File file = new File(path);
        File[] childFiles = file.listFiles();
        if (childFiles != null) {
            for (File childFile : childFiles) {
                if (childFile.isDirectory()) {
                    if (childPackage) {
                        String childPath = childFile.getName();
                        if (StringUtils.isNotBlank(packageName)) {
                            childPath = packageName + "." + childPath;
                        }
                        List<Class<?>> list = getDirectoryPathClasses(childFile.getPath(), childPath);
                        classes.addAll(list);
                    }
                } else {
                    String name = childFile.getName();
                    if (!name.endsWith(".class")) {
                        continue;
                    }
                    String className = name.substring(0, name.indexOf(".class"));
                    if (StringUtils.isNoneBlank(packageName)) {
                        className = packageName + "." + className;
                    }
                    Class<?> clazz;
                    try {
                        clazz = loader.loadClass(className);
                        classes.add(clazz);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return classes;
    }

}