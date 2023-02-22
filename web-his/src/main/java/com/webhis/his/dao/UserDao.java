package com.webhis.his.dao;

import com.webhis.his.domain.User;

import java.util.List;
import java.util.Map;

public interface UserDao {
    User findByUname(String uname);
    void updatePwd(long uid,String upass);
    User findById(long uid);
    public long listTotal(Map<String,Object> param);
    List<User> list(Map<String,Object> param);
    Long findTotalByUname(String uname);

    void delete(Map<String, Long> param);

    void update(User user);

    void save(User user);
}
