package com.phongvu.restapi.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserResponse {
    private String  id;
    private String username;
    private String fullName;
    private LocalDate dob;
    Set<String> roles;
}
