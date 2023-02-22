package com.webhis.his.dao.impl;

import com.webhis.his.dao.UserDao;
import com.webhis.his.domain.User;
import com.webhis.his.util.SqlSessionFactoryUtil;
import com.webhis.his.util.StringUtil;
import org.webhis.jdbc.SqlSession;
import org.webhis.jdbc.SqlSessionFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDaoImpl implements UserDao {
    @Override
    public User findByUname(String uname) {
        String sql="select" +
                " uid,uname,zjm,upass,phone,mail,sex,age," +
                " create_time,create_uid,update_time,update_uid," +
                " delete_flag,yl1,yl2,yl3,yl4" +
                " from " +
                " t_user " +
                " where " +
                " uname=#{uname} or zjm=#{uname} or phone=#{uname} or mail=#{uname}";
        SqlSessionFactory sqlSessionFactory = SqlSessionFactoryUtil.getDefaultFactory();
        SqlSession session = sqlSessionFactory.getSession(true);
        Map<String, String> map = new HashMap<>();
        //为了规避底层可能存在的问题，在一个参数需要使用多次的情况下，组成map或对象
        map.put("uname",uname);
        User user = session.selectOne(sql, map, User.class);
        return user;
    }



    @Override
    public void updatePwd(long uid, String upass) {
        String sql="update t_user set upass=#{upass} ," +
                "update_time=now(),update_uid=#{uid} where uid=#{uid}";
        SqlSessionFactory sqlSessionFactory = SqlSessionFactoryUtil.getDefaultFactory();
        SqlSession session = sqlSessionFactory.getSession(true);
        Map<String,Object> paramMap=new HashMap<>();
        paramMap.put("uid",uid);
        paramMap.put("upass",upass);
        session.update(sql,paramMap);
    }

    @Override
    public User findById(long uid) {
        String sql="select" +
                " uid,uname,zjm,truename,upass,phone,mail,sex,age," +
                " create_time,create_uid,update_time,update_uid," +
                " delete_flag,yl1,yl2,yl3,yl4" +
                " from " +
                " t_user " +
                " where " +
                " uid=#{uid}";
        SqlSessionFactory sqlSessionFactory =  SqlSessionFactoryUtil.getDefaultFactory();
        SqlSession session = sqlSessionFactory.getSession(true);
        Map<String, Long> map = new HashMap<>();
        //为了规避底层可能存在的问题，在一个参数需要使用多次的情况下，组成map或对象
        map.put("uid",uid);
        User user = session.selectOne(sql, map, User.class);
        return user;
    }
    @Override
    public long listTotal(Map<String, Object> param) {
        String sql="select count(*) from t_user u where u.delete_flag=1 ";
        sql=appendSqlWhere(param, sql);
        SqlSessionFactory sqlSessionFactory =  SqlSessionFactoryUtil.getDefaultFactory();
        SqlSession session = sqlSessionFactory.getSession(true);
        return session.selectOne(sql, param, Long.class);
    }

    @Override
    public List<User> list(Map<String, Object> param) {
        String sql="select u.* " +
                ",ifnull((select uname from t_user where uid=u.create_uid),'系统管理员') as create_uname," +
                "ifnull((select uname from t_user where uid=u.update_uid),'') as update_uname " +
                "from t_user u " +
                "where u.delete_flag=1 ";
        sql=appendSqlWhere(param, sql);
        sql+="order by ifnull(u.update_time,u.create_time) desc ";
        sql+="limit #{start},#{length}";
        SqlSessionFactory sqlSessionFactory =  SqlSessionFactoryUtil.getDefaultFactory();
        SqlSession session = sqlSessionFactory.getSession(true);
        return session.selectList(sql, param, User.class);
    }

    @Override
    public Long findTotalByUname(String uname) {
        String sql="select" +
                " count(*) " +
                " from " +
                " t_user " +
                " where " +
                " uname=#{uname} or zjm=#{uname} or phone=#{uname} or mail=#{uname}";
        SqlSessionFactory sqlSessionFactory =  SqlSessionFactoryUtil.getDefaultFactory();
        SqlSession session = sqlSessionFactory.getSession(true);
        Map<String, String> map = new HashMap<>();
        //为了规避底层可能存在的问题，在一个参数需要使用多次的情况下，组成map或对象
        map.put("uname",uname);
        Long l= session.selectOne(sql, map, Long.class);
        return l;
    }


    private static String appendSqlWhere(Map<String, Object> param, String sql) {
        String uname = (String) param.get("uname");
        if (StringUtil.isNotEmpty(uname)){
            sql +="and (u.uname like concat(#{uname},'%') or u.zjm like concat(#{uname},'%'))";
        }
        String phone= (String) param.get("phone");
        if (StringUtil.isNotEmpty(phone)){
            sql +="and u.phone like concat(#{phone},'%')";
        }
        return sql;
    }

    @Override
    public void delete(Map<String, Long> param) {
        String sql="update t_user set delete_flag=2,update_time=now(),update_uid=#{update_uid} where uid=#{uid}";
        SqlSessionFactory defaultFactory = SqlSessionFactoryUtil.getDefaultFactory();
        SqlSession session = defaultFactory.getSession(true);
        session.update(sql,param);
    }

    @Override
    public void update(User user) {
        String sql="update t_user set truename=#{truename},age=#{age},phone=#{phone},sex=#{sex},mail=#{mail},update_time=now(),update_uid=#{update_uid},yl1=#{yl1},yl2=#{yl2},yl3=#{yl3},yl4=#{yl4} where uid=#{uid}";
        SqlSessionFactory defaultFactory = SqlSessionFactoryUtil.getDefaultFactory();
        SqlSession session = defaultFactory.getSession(true);
        session.update(sql,user);
    }

    @Override
    public void save(User user) {
        String sql="insert into t_user " +
                "(uname,upass,zjm,truename,age,sex,phone,mail,create_time,create_uid,delete_flag) " +
                "values " +
                "(#{uname},#{upass},#{zjm},#{truename},#{age},#{sex},#{phone},#{mail},now(),#{create_uid},#{delete_flag})";
        SqlSessionFactory sqlSessionFactory = SqlSessionFactoryUtil.getDefaultFactory();
        SqlSession session = sqlSessionFactory.getSession(true);
        session.insert(sql, user);
    }
}
