package com.rtsp.param;

import java.util.concurrent.ConcurrentHashMap;

public class SessionManage {

    public static SessionManage sessionManage = new SessionManage();
    //存储用户ID对应信息会话
    private ConcurrentHashMap<String, IoSession> userSessionMap = new ConcurrentHashMap<String, IoSession>();

    public static SessionManage getInstance() {
        return sessionManage;
    }

    public ConcurrentHashMap getUserSessionMap() {
        return userSessionMap;
    }

}
