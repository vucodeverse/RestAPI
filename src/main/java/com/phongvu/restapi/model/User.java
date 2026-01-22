package com.phongvu.restapi.model;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String  id;
    private String username;
    private String password;
    private String fullName;
    private LocalDate dob;
}
