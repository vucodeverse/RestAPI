package com.phongvu.restapi.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AuthenticationResponse {
    private String token;
    private boolean isAuthenticated;
}
