package com.phongvu.restapi.utils.annotation;

import java.lang.annotation.*;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    // Mã quyền, dùng để check (VD: USER_CREATE)
    String code();
    // Tên quyền hiển thị trên UI cho Admin (VD: "Tạo mới người dùng")
    String name();
    // Thuộc module nào (VD: "Quản lý Người dùng")
    String module();
}
