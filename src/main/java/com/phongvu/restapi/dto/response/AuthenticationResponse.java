package com.phongvu.restapi.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class AuthenticationResponse {
    String token;
    boolean authenticated;
    boolean mfaRequired;
}
