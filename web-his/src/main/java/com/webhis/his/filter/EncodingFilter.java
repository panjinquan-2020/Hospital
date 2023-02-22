package com.webhis.his.filter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//过滤编码
@WebFilter(filterName = "EncodingFilter",
        urlPatterns = "/*",
        initParams = @WebInitParam(name = "encoding", value = "utf-8"))
public class EncodingFilter extends HttpFilter {
    String encoding;
    @Override
    public void init() throws ServletException {
        encoding = this.getInitParameter("encoding");
        if (encoding==null&&encoding.equals("")){
            encoding="utf-8";
        }
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding("utf-8");
        chain.doFilter(request,response);
    }
}
