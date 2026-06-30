package com.phongvu.restapi.configuration;

import com.phongvu.restapi.model.Permission;
import com.phongvu.restapi.repository.PermissionRepository;
import com.phongvu.restapi.utils.annotation.RequirePermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionSeeder implements ApplicationRunner {
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final PermissionRepository permissionRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Bắt đầu quét và đồng bộ API Permissions...");
        // Lấy tất cả các API đã được đăng ký trong Spring
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            // Tìm annotation @RequirePermission trên hàm
            RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
            if (requirePermission != null) {
                RequestMappingInfo mappingInfo = entry.getKey();
                // Trích xuất thông tin API
                String apiPath = mappingInfo.getPatternValues().iterator().next(); // Lấy path đầu tiên
                String httpMethod = mappingInfo.getMethodsCondition().getMethods().iterator().next().name();
                String code = requirePermission.code();
                // Lưu hoặc cập nhật vào DB
                Permission permission = permissionRepository.findByCode(code).orElse(new Permission());
                permission.setCode(code);
                permission.setName(requirePermission.name());
                permission.setModule(requirePermission.module());
                permission.setApiPath(apiPath);
                permission.setMethod(httpMethod);
                permissionRepository.save(permission);
                log.info("Synced Permission: {} - {} {}", code, httpMethod, apiPath);
            }
        }
        log.info("Đồng bộ API Permissions thành công!");
    }
}
