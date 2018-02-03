package org.kaze.framework.mvc.annotation;

import java.lang.annotation.*;

/**
 * 返回字符串
 *
 * @author kaze
 * @since 2017/09/02
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseBody {

}
