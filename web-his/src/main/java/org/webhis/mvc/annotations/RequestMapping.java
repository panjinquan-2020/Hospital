package org.webhis.mvc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//使用Target元注解 指定当前自定义注解可以作用在哪些内容上
@Target(ElementType.METHOD) //可以在方法上写当前注解
//使用Retention元注解 指定当前自定义注解的生生命周期
@Retention(RetentionPolicy.RUNTIME)//程序运行时，可以反射使用
public @interface RequestMapping {
   String[] value() ;
}
