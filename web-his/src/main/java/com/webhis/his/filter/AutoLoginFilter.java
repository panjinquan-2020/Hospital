package com.webhis.his.filter;


import com.sun.net.httpserver.Filter;
import com.webhis.his.util.AutoLoginUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//过滤自动登录
@WebFilter({"/login.jsp","/login"})
public class AutoLoginFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (AutoLoginUtil.isAutoLogin(request,response)){
            request.getRequestDispatcher("main.jsp").forward(request,response);
            return;
        }
        chain.doFilter(request,response);
    }
}
