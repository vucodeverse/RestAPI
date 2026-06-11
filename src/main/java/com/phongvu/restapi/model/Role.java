package com.phongvu.restapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "tbl_roles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Role {
    @Id
    private Integer id;
    private String name;
    private String description;
    @ManyToMany
    Set<Permission> permissions;
}
