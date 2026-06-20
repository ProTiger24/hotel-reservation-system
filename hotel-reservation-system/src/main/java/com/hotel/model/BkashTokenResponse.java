package com.hotel.model;

import lombok.Data;

@Data
public class BkashTokenResponse {
    private String id_token;
    private String token_type;
    private String expires_in;
    private String refresh_token;
    private String statusMessage;
}
