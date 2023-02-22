package com.webhis.his.util;
//判断字符串是否为空
public class StringUtil {
    public static boolean isEmpty(String s){
        return s==null||s.equals("");
    }
    public static boolean isNotEmpty(String s){
        return !isEmpty(s);
    }
}
