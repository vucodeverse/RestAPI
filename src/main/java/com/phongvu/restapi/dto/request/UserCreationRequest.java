package com.phongvu.restapi.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be >= {min} & <= {max} characters")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9._-]{2,}$", message = "Username must start with a letter and contain only letters, digits, dots, underscores, or hyphens")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 5, message = "Password must be >= {min} characters")
    private String password;

    private String fullName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
}
