package org.webhis.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SqlSessionFactory {

    //一个工厂读取一个配置文件，需要一个cfg对象
    private Configuration cfg = new Configuration();

    //默认读取src/jdbc.properties
    public SqlSessionFactory(){
        //读一次配置文件
        this("jdbc.properties") ;
    }

    //读取src目录指定名称的文件
    public SqlSessionFactory(String filename){
        this(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(filename));
    }

    //读取指定目录的文件
    //InputStream is = new FileInputStream("d:/z/jdbc.properties")
    public SqlSessionFactory( InputStream is){
        //最终读取配置文件的位置
        try {
            Properties p = new Properties();
            p.load(is);
            cfg.setDriverClassName( p.getProperty("driverClassName") );
            cfg.setUrl( p.getProperty("url") );
            cfg.setUsername( p.getProperty("username") );
            cfg.setPassword( p.getProperty("password") );

            this.classForName();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void classForName(){
        try {
            Class.forName(cfg.getDriverClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    //创建默认手动处理事务的session
    public SqlSession getSession(){
        return getSession(false) ;
    }

    //根据需求创建手动false/自动true的session
    public SqlSession getSession(boolean isAutoCommit){
        return new SqlSession(cfg,isAutoCommit);
    }

}
