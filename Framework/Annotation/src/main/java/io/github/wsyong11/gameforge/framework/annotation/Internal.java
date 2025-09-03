package io.github.wsyong11.gameforge.framework.annotation;

import java.lang.annotation.*;

/**
 * 带有此注解的类以及软件包等应视为内部组件，不应在所属模块之外使用
 */
@Documented
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Internal {
}
