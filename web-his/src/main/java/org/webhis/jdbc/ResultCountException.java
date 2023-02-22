package org.webhis.jdbc;

/**
 * selectOne查询时，查询结果多于1条时抛出该异常
 */
public class ResultCountException extends RuntimeException {
    public ResultCountException(){}
    public ResultCountException(String msg){ super(msg) ;}
}
