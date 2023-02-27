package com.rtsp.util;

import java.io.*;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: LinQ
 * Date: 2017/12/6
 * Description:
 */
public class PropsConfig {
    private static final PropsConfig instance = new PropsConfig();
    private Properties props;

    /**
     * 生成属性列表
     */
    private PropsConfig() {
        props = new Properties();
        InputStream in = null;
        try {
            in = PropsConfig.class.getResourceAsStream("rtsp.properties");
            if (in == null) {
                //测试环境下 加/src/main/resources/
                in = new BufferedInputStream(new FileInputStream(new File(System.getProperty("user.dir") + "/rtsp.properties")));
                // 加载属性列表
                props.load(in);
            } else {
                in = new BufferedInputStream(in);
                // 加载属性列表
                props.load(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static PropsConfig getInstance() {
        return instance;
    }

    public String get(String key) {
        return props.getProperty(key);
    }
}