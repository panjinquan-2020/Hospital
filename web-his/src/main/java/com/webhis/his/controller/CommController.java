package com.webhis.his.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import com.webhis.his.domain.User;
import com.webhis.his.service.UserService;
import com.webhis.his.service.impl.UserServiceImpl;
import org.webhis.mvc.annotations.RequestMapping;
import org.webhis.mvc.annotations.RequestParam;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

public class CommController {
    private static final int VERCODE_ERROR=9;
    private static final int UNAME_ERROR=8;
    private static final int UPASS_ERROR=7;
    private UserService userService=new UserServiceImpl();

    //添加验证码
    @RequestMapping("/verifyCode")
    public void verifyCode(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //定义图形验证码的长、宽、验证码字符数、干扰元素个数
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(120, 40, 4, 20);
        //获得生成的验证码
        String code = captcha.getCode();
        //获得的验证码响应给浏览器
        captcha.write(response.getOutputStream());
        //图形验证码写出，可以写出到文件，也可以写出到流
        //captcha.write("d:/circle.png");
        //生成的验证码需要在后面登录操作时进行验证
        HttpSession session = request.getSession();
        session.setAttribute("code",code);
    }

    //登录
    @RequestMapping("/login")
    public String login(@RequestParam("uname") String uname,
                        @RequestParam("upass") String upass,
                        @RequestParam("vercode") String vercode,
                        @RequestParam("remember") String remember,
                        HttpServletRequest request,HttpServletResponse response,HttpSession session){
//        if (uname==null||uname.equals("")){
//
//        }
        //检测验证码
        String code= (String) session.getAttribute("code");
        //验证码错误，重新刷新登录页面
        if (!vercode.equals(code)){
            //return "login.jsp?f="+VERCODE_ERROR;
            return reloadLoginUrl(VERCODE_ERROR);
        }
        //检测账号+密码
        //账号+密码一起检测 以账号和密码作为条件查找符合条件的记录
        //先根据账号再判断密码
        User byUname = userService.findByUname(uname);
        if (byUname==null){
            //没有找到用户，直接刷新页面
            //return "login.jsp?f="+UNAME_ERROR;
            return reloadLoginUrl(UNAME_ERROR);
        }
        //判断密码
        if (!byUname.getUpass().equals(upass)){
            //return "login.jsp?f="+UPASS_ERROR;
            return reloadLoginUrl(UPASS_ERROR);
        }

        //将登录信息存入session中
        session.setAttribute("user",byUname);
        //如果勾选记住密码
        executeAutoLogin(remember, byUname.getUid(), request,response);

        return "logSuccess.jsp";
    }

    //注销
    @RequestMapping("/exit")
    public String exit(HttpSession session){
        //清除所有的session
        session.invalidate();
        return "login.jsp";
    }

    //私有方法 拼接字符串
    private String reloadLoginUrl(int code){
        return "login.jsp?f="+code;
    }
    //私有方法 设置cookie
    private void executeAutoLogin(String remenber,long uid,HttpServletRequest request,HttpServletResponse response){
        if (remenber==null||remenber.equals("")){
            //没有勾选记住密码
            return;
        }
        //随机生成一个状态码
        String token= UUID.randomUUID().toString().replace("-","");
        token+="-"+System.currentTimeMillis();
        //添加cookie
        Cookie cookie = new Cookie("token",token);
        cookie.setMaxAge(30);
        response.addCookie(cookie);
        //将信息保存到application中
        ServletContext application = request.getServletContext();
        application.setAttribute(token,uid);
    }
}
