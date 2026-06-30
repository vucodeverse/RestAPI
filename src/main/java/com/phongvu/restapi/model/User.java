package com.phongvu.restapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "tbl_users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String password;

    @Column(name = "totp_secret")
    private String totpSecret;

    @Column(name = "is_2fa_enabled", nullable = false)
    @Builder.Default
    private boolean is2faEnabled = false;

    private String fullName;
    private LocalDate dob;
    @ManyToMany
    private Set<Role> roles;
}
