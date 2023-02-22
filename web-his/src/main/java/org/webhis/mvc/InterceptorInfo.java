package org.webhis.mvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 存储<interceptor>标签信息
 */
public class InterceptorInfo {

    private String classname ;
    private Set<String> include ;
    private Set<String> exclude ;

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public Set<String> getInclude() {
        return include;
    }

    public void setInclude(String include) {
        //没有配置该属性
        if(include == null || "".equals(include))
            return ;

        //配置了属性
        String[] ss = include.split(",");
        this.include = new HashSet<>(  Arrays.asList(ss) ) ;
    }

    public Set<String> getExclude() {
        return exclude;
    }

    public void setExclude(String exclude) {
        //没有配置该属性
        if(exclude == null || "".equals(exclude))
            return ;

        //配置了属性
        String[] ss = exclude.split(",");
        this.exclude = new HashSet<>(  Arrays.asList(ss) ) ;
    }
}
