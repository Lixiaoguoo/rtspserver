package com.rtsp.param;

import java.util.HashMap;

public class User {
    private int userId;
    private String userName;
    private String passWord;


    public void setData(HashMap map){
        if(map == null){
            return;
        }


    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
