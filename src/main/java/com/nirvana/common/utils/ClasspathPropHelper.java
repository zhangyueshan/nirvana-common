package com.nirvana.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by InThEnd on 2016/5/4.
 * properties文件工具类。
 */
public class ClasspathPropHelper {

    private Properties prop;

    public ClasspathPropHelper(String name) {
        prop = new Properties();
        InputStream fis;
        try {
            fis = this.getClass().getClassLoader().getResourceAsStream(name);
            Assert.notNull(fis, "classpath项目根目录下：" + name + "文件不存在。");
            prop.load(fis);
            fis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getProperty(String key) {
        return prop.getProperty(key);
    }
}
