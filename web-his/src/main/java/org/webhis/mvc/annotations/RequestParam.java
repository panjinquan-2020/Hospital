package org.webhis.mvc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) //该注解只能作用在参数上面
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
    String value() ; //传递参数名
}
