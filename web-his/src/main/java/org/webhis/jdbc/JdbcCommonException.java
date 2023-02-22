package org.webhis.jdbc;

/**
 * jdbc框架异常统一描述对象
 *
 */
public class JdbcCommonException extends RuntimeException{
    public JdbcCommonException(){}

    public JdbcCommonException(String s){
        super(s) ;
    }
}
