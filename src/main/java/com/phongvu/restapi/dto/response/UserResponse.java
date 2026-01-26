package com.phongvu.restapi.dto.response;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserResponse {
    private String  id;
    private String username;
    private String password;
    private String fullName;
    private LocalDate dob;
}
