package com.webhis.his.service.impl;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.webhis.his.VO.pageVO;
import com.webhis.his.dao.UserDao;
import com.webhis.his.dao.impl.UserDaoImpl;
import com.webhis.his.domain.User;
import com.webhis.his.service.UserService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    private static Digester md5 = new Digester(DigestAlgorithm.MD5);
    private static String PASS="123";
    UserDao userDao = new UserDaoImpl();

    @Override
    public User findByUname(String uname) {
        return userDao.findByUname(uname);
    }

    @Override
    public void updatePwd(long uid, String uname) {
        userDao.updatePwd(uid, uname);
    }

    @Override
    public pageVO list(Map<String, Object> param) {
        Integer page = (Integer) param.get("page");
        Integer rows = (Integer) param.get("rows");
        //处理下限 不会少于1
        page = Math.max(1, page);
        //处理上限 不会多于
        //需要查询总数 如果有过滤条件需要计算
        long total = userDao.listTotal(param);
        int max = (int) (total % rows == 0 ? total / rows : total / rows + 1);
        max = Math.max(1, max);
        page = Math.min(page, max);
        param.put("page", page);
        //查询
        int start=(page-1)*rows;
        int length=rows;
        param.put("start",start);
        param.put("length",length);
        List<User> list = userDao.list(param);
        return new pageVO(list,total,rows,page,param,max);
    }

    @Override
    public void delete(Map<String, Long> param) {
        userDao.delete(param);
    }

    @Override
    public void deletes(Map<String, Object> param) {
        String uids= (String) param.get("uids");
        String[] uidArray=uids.split(",");
        Map<String,Long> deleteMap=new HashMap<>();
        deleteMap.put("update_uid", (Long) param.get("update_uid"));
        for (String uid:uidArray){
            deleteMap.put("uid", Long.valueOf(uid));
            userDao.delete(deleteMap);
        }
    }

    @Override
    public User findById(long uid) {
        return userDao.findById(uid);
    }

    @Override
    public String update(User user) {
        User old = userDao.findById(user.getUid());
        if (!user.getPhone().equals(old.getPhone())){
            if (userDao.findTotalByUname(user.getPhone())>=1){
                return "phone";
            }
        }
        if (!user.getMail().equals(old.getMail())){
            if (userDao.findTotalByUname(user.getMail())>=1){
                return "mail";
            }
        }
        userDao.update(user);
        return "";
    }

    @Override
    public String saves(List<User> users,long create_uid) {
        String tip="";
        int i=1;
        for (User user:users){
            String result=save(user,create_uid);
            switch (result){
                case "uname":tip+="第【"+i+"】条件，用户名【"+user.getUname()+"】重复";break;
                case "zjm":tip+="第【"+i+"】条件，助记码【"+user.getZjm()+"】重复";break;
                case "phone":tip+="第【"+i+"】条件，电话【"+user.getPhone()+"】重复";break;
                case "mail":tip+="第【"+i+"】条件，邮箱【"+user.getMail()+"】重复";break;
            }
            i++;
        }
        return tip;
    }

    @Override
    public String save(User user, long create_uid) {
        String zjm= PinyinUtil.getPinyin(user.getUname(),"");
        user.setZjm(zjm);
        String upass= md5.digestHex(PASS);
        user.setUpass(upass);
        user.setCreate_uid(create_uid);
        user.setCreate_time(new Date());
        user.setDelete_flag(1);
        if (userDao.findTotalByUname(user.getUname())>=1){
            return "uname";
        }
        if (userDao.findTotalByUname(user.getZjm())>=1){
            return "zjm";
        }
        if (userDao.findTotalByUname(user.getPhone())>=1){
            return "phone";
        }
        if (userDao.findTotalByUname(user.getMail())>=1){
            return "mail";
        }
        userDao.save(user);
        return "";
    }
}
