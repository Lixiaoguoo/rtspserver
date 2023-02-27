package com.rtsp.util;

import java.util.HashMap;
import java.util.Map;

public class TruncateUrl {

    /**
     * 解析出url参数中的键值对
     * 如 "xxx?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     */
    public static Map<String, Object> URLRequest(String URL) {
        Map<String, Object> mapRequest = new HashMap<String, Object>();

        String[] arrSplit = null;

        String strUrlParam = TruncateStrUrl(URL);
        if (strUrlParam == null) {
            return mapRequest;
        }
        //每个键值为一组
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");

            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

            } else {
                if (arrSplitEqual[0] != "") {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String TruncateStrUrl(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;

        strURL = strURL.trim();

        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                }
            }
        }
        return strAllParam;
    }

    /**
     * 获取URL方法Str
     */
    public static String getActionName(String strURL){
        int lastXGIndex = strURL.lastIndexOf("/");
        int lastWHIndex = strURL.lastIndexOf("?");
        return strURL.substring(lastXGIndex + 1,lastWHIndex);
    }

    public static void main(String[] args) {
       // System.out.println(TruncateUrl.getActionName("rtsp://username:password@ip:port/playback?devid=12344&ch=1&stream=1&type=0&mem=0&start=20200217160403&end=20200217160403&playtype=0&playspeed=1"));
    }
}
