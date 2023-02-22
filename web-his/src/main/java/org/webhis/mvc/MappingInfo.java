package org.webhis.mvc;

import java.lang.reflect.Method;

/**
 * 存储xml/注解中配置的一个请求映射信息
 * domain对象
 */
public class MappingInfo {

    //请求路径  /test1
    private String path ;

    //请求的目标 controller对象
    private Object controller ;

    //请求的方法 controller.方法
    //利用反射的Method类表示请求的方法
    //因为在实际运行的过程中，要根据请求，调用这个方法，需要使用反射。
    //Method表示的方法自然包含方法名字，返回类型，参数列表
    private Method method ;

    public MappingInfo(String path, Object controller, Method method) {
        this.path = path;
        this.controller = controller;
        this.method = method;
    }

    public MappingInfo() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
