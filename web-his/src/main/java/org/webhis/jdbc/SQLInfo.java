package org.webhis.jdbc;

/**
 * 存储sql处理器处理后的sql信息
 */
public class SQLInfo {
    //可以执行sql，带有?
    private String sql ;
    //装在原sql中#{}设置的那些key ["cname","color","price"]
    private String[] kyes ;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String[] getKyes() {
        return kyes;
    }

    public void setKyes(String[] kyes) {
        this.kyes = kyes;
    }

    public SQLInfo(String sql, String[] kyes) {
        this.sql = sql;
        this.kyes = kyes;
    }

    public SQLInfo() {
    }
}
