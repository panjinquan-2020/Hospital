package com.webhis.his.util;

import com.webhis.his.dao.UserDao;
import com.webhis.his.dao.impl.UserDaoImpl;
import com.webhis.his.domain.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//自动登录验证
public class AutoLoginUtil {
    private static UserDao dao=new UserDaoImpl();
    public static boolean isAutoLogin(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie:cookies){
            if (cookie.getName().equals("token")){
                //找到cookie信息
                String value = cookie.getValue();
                long time = Long.parseLong(value.split("-")[1]);
                //获得登录信息
                Long uid = (Long) request.getServletContext().getAttribute(value);
                if (uid==null){
                    response.setHeader("auto-login-info","auto login invalid");
                    return false;
                }
                long curr = System.currentTimeMillis();
                long day=(curr-time)/1000/60/60/24;
                if (day>7){
                    //过期 不能自动登录
                    response.setHeader("auto-login-info","auto login invalid,token expire");
                    return false;
                }
                User user = dao.findById(uid);
                if (user==null){
                    response.setHeader("auto-login-info","auto login fall,user not exist");
                    return false;
                }
                addLoginUser(user, request.getSession());
                return true;
            }
        }
        //什么也没找到
        return false;
    }
    public static void addLoginUser(User user, HttpSession session){
        session.setAttribute("user",user);
    }
}
