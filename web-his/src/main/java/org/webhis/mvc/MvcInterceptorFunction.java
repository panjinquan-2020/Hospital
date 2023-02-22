package org.webhis.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 切面功能的规范
 * 框架的使用者根据这个规范提供切面功能
 */
public interface MvcInterceptorFunction {
    //符合当前规则的方法，可以在目标前被调用
    //return true 验证通过，继续请求。 false 验证失败，终止请求
    public boolean prev(HttpServletRequest req, HttpServletResponse resp, Object target)throws Exception;

    //符合当前规则的方法，可以在目标后被调用
    public void post(HttpServletRequest req, HttpServletResponse resp)throws Exception;
}
