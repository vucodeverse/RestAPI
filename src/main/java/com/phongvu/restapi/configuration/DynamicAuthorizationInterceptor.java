package com.phongvu.restapi.configuration;

import com.phongvu.restapi.utils.annotation.RequirePermission;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class DynamicAuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Nếu không phải là một method trong Controller thì bỏ qua
        if (!(handler instanceof HandlerMethod)) return true;
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        // API không có đánh dấu @RequirePermission -> Cho phép truy cập tự do (hoặc tuỳ chiến lược của bạn)
        if (requirePermission == null) return true;
        String requiredCode = requirePermission.code();
        // Lấy thông tin user hiện tại từ SecurityContext (đã được parse từ JWT trước đó)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // GIẢ SỬ bạn đã lưu danh sách quyền (code) của User vào Spring Security Authorities
        // Hoặc bạn truy vấn từ Service có Cache: List<String> userPermissions = roleService.getUserPermissions(userId);
        boolean hasPermission = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(requiredCode));

        if (!hasPermission) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện thao tác này!");
            return false; // Chặn request
        }
        return true; // Cho phép đi tiếp vào Controller
    }
}