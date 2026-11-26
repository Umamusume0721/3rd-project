package com.teamcollab.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口权限声明。
 * 用法：@RequirePermission("user:assign-role")，多个权限点为“或”关系。
 * 可标注在方法或类上，方法上的优先。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

    String[] value();
}
