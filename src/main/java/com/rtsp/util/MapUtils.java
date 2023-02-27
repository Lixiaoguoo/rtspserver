package com.rtsp.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class MapUtils {

    private static final String INT_NAME = "int";
    private static final String SRTING_NAME = "String";
    private static final String LONG_NAME = "long";

    //把Map转化为JavaBean
    public static <T> T map2bean(Map<String, Object> map, Class<T> clz) {
        //创建一个需要转换为的类型的对象
        T obj = null;
        //从Map中获取和属性名称一样的值，把值设置给对象(setter方法)
        try {
            obj = clz.newInstance();
            //得到属性的描述器
            BeanInfo b = Introspector.getBeanInfo(clz, Object.class);
            PropertyDescriptor[] pds = b.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                //得到属性的setter方法
                Method setter = pd.getWriteMethod();
                Object objValue = map.get(pd.getName());
                if (objValue == null) {
                    continue;
                }
                //得到key名字和属性名字相同的value设置给属性
                if (SRTING_NAME.equals(pd.getPropertyType().getSimpleName())) {
                    setter.invoke(obj, objValue.toString());
                }
                if (INT_NAME.equals(pd.getPropertyType().getSimpleName())) {
                    setter.invoke(obj, Integer.parseInt(objValue.toString()));
                }
                if (LONG_NAME.equals(pd.getPropertyType().getSimpleName())) {
                    setter.invoke(obj, Long.valueOf(objValue.toString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

}
