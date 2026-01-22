package com.phongvu.restapi.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class UserUpdateRequest {
    private String password;
    private String fullName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
}
