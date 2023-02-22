package com.webhis.his.filter;

import com.webhis.his.domain.User;
import com.webhis.his.util.AutoLoginUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
//过滤登录 判断用户是否改变
@WebFilter("/*")
public class LoginFilter extends HttpFilter {
    String[] excludes=new String[]{"login.jsp","login","timeout.jsp"
            ,"exit","*.js","*.css","*.png","*.jpg","verifyCode"
            ,"*.eot","*.svg","*.ttf","*.woff","*.woff2","forget.jsp","forget"
            ,"updatePwd.htm","mailTip.jsp","updatePwd"};
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String servletPath = request.getServletPath();
        for (String s:excludes){
            if (servletPath.startsWith("/")){
                //字符串首位有/ 去除
                servletPath=servletPath.substring(1);
            }
            if(s.startsWith("*")){
                //候选字符串中有* 去除
                s=s.substring(1);
                if (servletPath.endsWith(s)){
                    //字符串的末尾含有候选字符串 通过
                    chain.doFilter(request,response);
                    return;
                }
            }else {
                //没有*
                if (s.equals(servletPath)){
                    //字符串和候选字符串相同 通过
                    chain.doFilter(request,response);
                    return;
                }
            }
        }

        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("user");
        if (loginUser!=null){
            //如果验证通过 放过请求 就绪完成这次的请求操作
            chain.doFilter(request,response);
        }else {
            //自动登录判断
            if (AutoLoginUtil.isAutoLogin(request, response)){
                chain.doFilter(request,response);
            }

            request.getRequestDispatcher("timeout.jsp").forward(request,response);
        }
    }
}
