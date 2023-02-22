package org.webhis.jdbc;

/**
 * sql格式异常
 * 执行的sql与执行的方法不匹配时抛出该异常
 */
public class SQLFormatException extends RuntimeException{
    public SQLFormatException(){}
    public SQLFormatException(String e){super(e);}
}
