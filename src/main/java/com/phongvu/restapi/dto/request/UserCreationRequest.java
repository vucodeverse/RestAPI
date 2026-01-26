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

    @NotBlank(message = "Username is not required")
    @Size(min = 5, max = 100, message = "Username must be >= {min} $ <= {max}")
    @Pattern(
            regexp = "^([A-Z][a-z]+)(\\s[A-Z][a-z]+)*$",
            message = "The Username is invalid. Each word must begin with a capital letter, " +
                    "contain only alphabetic characters, and must not include numbers, " +
                    "special characters, or extra spaces."
    )
    private String username;
    @NotBlank(message = "Password is not required")
    @Size(min = 5, message = "Password must be >= {min} character")
    private String password;

    private String fullName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
}
