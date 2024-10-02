package com.alfredoeka.spring_assignment_five.model.oauth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponse {
    private String access_token;
    private String refresh_token;
    private String scope;
    private String token_type;
    private long expires_in;
    private String jti;
}
