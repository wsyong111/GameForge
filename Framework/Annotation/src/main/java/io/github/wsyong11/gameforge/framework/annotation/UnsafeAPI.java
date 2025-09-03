package io.github.wsyong11.gameforge.framework.annotation;

import java.lang.annotation.*;

/**
 * 带有此注解的 类，字段，函数，构造函数，软件包和模块 为不安全API，在未充分了解其作用前使用导致的问题后果自负
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.PACKAGE, ElementType.MODULE, ElementType.TYPE})
public @interface UnsafeAPI {
}
