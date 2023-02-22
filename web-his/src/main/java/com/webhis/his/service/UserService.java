package com.webhis.his.service;

import com.webhis.his.VO.pageVO;
import com.webhis.his.domain.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User findByUname(String uname);
    void updatePwd(long uid,String uname);
    pageVO list(Map<String,Object> param);
    void delete(Map<String, Long> param);
    void deletes(Map<String, Object> param);
    User findById(long uid);
    String update(User user);
    String saves(List<User> users,long create_uid);
    String save(User user,long create_uid);
}
