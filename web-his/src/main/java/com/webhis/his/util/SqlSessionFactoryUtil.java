package com.webhis.his.util;

import org.webhis.jdbc.SqlSessionFactory;

public class SqlSessionFactoryUtil {
    private static SqlSessionFactory defaultFactory;
    static {
        defaultFactory=new SqlSessionFactory();
    }
    public static SqlSessionFactory getDefaultFactory(){
        return defaultFactory;
    }
}
