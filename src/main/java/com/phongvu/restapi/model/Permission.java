package com.phongvu.restapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_permissions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Permission extends BaseEntity {
    @Id
    private Integer id;
    private String name;
    private String description;
    @Column(unique = true, nullable = false)
    private String code; // Lấy từ thuộc tính 'code' của annotation
    private String apiPath; // Tự động lấy từ @PostMapping, @GetMapping...
    private String method; // HTTP Method (POST, GET, PUT...)
    private String module;
}
