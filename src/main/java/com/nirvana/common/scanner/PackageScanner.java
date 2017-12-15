package com.nirvana.common.scanner;

import com.nirvana.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Nirvana on 2017/12/14.
 */
public class PackageScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(PackageScanner.class);

    private static final String[] EXCLUDE_PACKAGES = new String[]{
            "com.sun",
            "sun",
            "jdk",
            "java",
            "javax",
            "javafx",
            "org",
            "com.intellij",
            "ch.qos.logback",
            "com.google",
            "com.thoughtworks",
            "com.oracle",
            "oracle",
            "com.microsoft",
            "net.sf",
            "netscape"
    };

    private String basePackage;
    private ClassLoader classLoader = getClass().getClassLoader();
    private ClassFilter classFilter;

    private boolean scanJar;

    public PackageScanner() {
        this(new TrueFilter());
    }

    public PackageScanner(ClassFilter filter) {
        this("", filter);
    }

    public PackageScanner(String basePackage, ClassFilter filter) {
        this(basePackage, true, filter);
    }

    public PackageScanner(String basePackage, boolean scanJar, ClassFilter filter) {
        this.basePackage = basePackage;
        this.scanJar = scanJar;
        this.classFilter = filter;
    }

    public Collection<Class<?>> scan() {
        try {
            Collection<Class<?>> classes = new ArrayList<>();
            URL[] urls = ((URLClassLoader) classLoader).getURLs();
            for (URL url : urls) {
                File file = new File(URLDecoder.decode(url.getFile(), "UTF8"));
                if (!file.isDirectory() && file.getName().endsWith(".jar")) {
                    if (scanJar) {
                        fuckJar(classes, new JarFile(file), classFilter);
                    }
                } else {
                    fuckFile(classes, file, "", classFilter);
                }
            }
            return classes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void fuckFile(Collection<Class<?>> classes, File file, String javaTypeName, ClassFilter filter) {
        if (!match(basePackage, javaTypeName)) {
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles != null) {
                for (File childFile : childFiles) {
                    String childPrefix = childFile.getName();
                    if (StringUtils.isNotBlank(javaTypeName)) {
                        childPrefix = javaTypeName + "." + childPrefix;
                    }
                    fuckFile(classes, childFile, childPrefix, filter);
                }
            }
        } else {
            if (javaTypeName.endsWith(".class")) {
                String className = javaTypeName.substring(0, javaTypeName.lastIndexOf(".class"));
                if (checkClassName(className)) {
                    loadClass(classes, className, filter);
                }
            }
        }
    }

    private void loadClass(Collection<Class<?>> classes, String className, ClassFilter filter) {
        try {
            Class<?> clazz = classLoader.loadClass(className);
            if (filter.leave(clazz)) {
                classes.add(clazz);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.debug("class not found exception: {}", className);
        } catch (NoClassDefFoundError e) {
            LOGGER.debug("no class def found error: {}", className);
        } catch (IncompatibleClassChangeError e) {
            LOGGER.debug("incompatible class change error: {}", className);
        }
    }

    private void fuckJar(Collection<Class<?>> classes, JarFile jarFile, ClassFilter filter) {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String entryName = jarEntry.getName();
            if (entryName.endsWith(".class")) {
                String className = entryName.replace("/", ".").substring(0, entryName.lastIndexOf(".class"));
                if (checkClassName(className)) {
                    loadClass(classes, className, filter);
                }
            }
        }
    }

    private boolean checkClassName(String className) {
        return (!StringUtils.isNoneBlank(basePackage) || className.startsWith(basePackage + ".")) && !inExcludePackages(className);
    }

    private boolean inExcludePackages(String className) {
        for (String string : EXCLUDE_PACKAGES) {
            if (className.startsWith(string + ".")) {
                return true;
            }
        }
        return false;
    }

    private boolean match(String string1, String string2) {
        for (int i = 0; i < Math.min(string1.length(), string2.length()); i++) {
            if (string1.charAt(i) != string2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

}
