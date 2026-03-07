package com.phongvu.restapi.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class IntrospectResponse {
    private boolean valid;
}
