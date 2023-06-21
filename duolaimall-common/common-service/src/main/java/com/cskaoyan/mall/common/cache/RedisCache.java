package com.cskaoyan.mall.common.cache;

import java.lang.annotation.*;

/**
 * @author VHcat 1377594091@qq.com
 * @since 2023/06/15 21:01
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RedisCache {

    // 给缓存数据增加前缀，以区分不同的缓存数据
    String prefix() default "cache:";

}
