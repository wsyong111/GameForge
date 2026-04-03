package io.github.wsyong11.gameforge.framework.annotation.nio;

import java.lang.annotation.*;
import java.nio.Buffer;

/**
 * 带有此注解的函数，类型，字段，变量，应保证 {@link Buffer} 为 {@link Buffer#isDirect() 直接内存访问}
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE_PARAMETER, ElementType.TYPE_USE, ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface DirectBuffer {
}
