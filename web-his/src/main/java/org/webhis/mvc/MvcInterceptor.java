package org.webhis.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 框架AOP结构中的：切面
 * 类似于之前的过滤器
 * 本来应该有程序员创建
 */
public class MvcInterceptor {

    private MvcInterceptorFunction function ;

    public MvcInterceptor(MvcInterceptorFunction function) {
        this.function = function;
    }

    public Object doFilter(HttpServletRequest req , HttpServletResponse resp, MvcInterceptorChain chain) throws Exception {
        //之前做些事
        boolean f = function.prev(req,resp,chain.getTargetInfo());

        if(!f)
            return null;

        Object result = chain.doFilter(req,resp);//调用下一个拦截器或目标

        //之后做些事
        function.post(req,resp);

        return result ;
    }

}
