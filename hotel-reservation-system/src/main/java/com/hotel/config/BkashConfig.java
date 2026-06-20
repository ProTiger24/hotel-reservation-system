package com.hotel.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class BkashConfig {
    
    // ✅ Tokenized Sandbox Base URL
    public static final String BKASH_SANDBOX_BASE_URL = "https://tokenized.sandbox.bka.sh/v1.2.0-beta/tokenized/checkout";
    
    // Endpoints
    public static final String GRANT_TOKEN_ENDPOINT = "/token/grant";
    public static final String CREATE_AGREEMENT_ENDPOINT = "/agreement/create";
    public static final String EXECUTE_AGREEMENT_ENDPOINT = "/agreement/execute";
    public static final String PAYMENT_WITH_AGREEMENT_ENDPOINT = "/payment/create";
    
    // Sandbox Credentials
    public static final String APP_KEY = "4f6o0cjiki2rfm34kfdadl1eqq";
    public static final String APP_SECRET = "2isukhdgtq9ek7r8d0qwg2omg3d0e4o4o4o4o4o4";
    public static final String USERNAME = "sandboxTokenizedUser01";
    public static final String PASSWORD = "sandboxTokenizedUser01@12345";
    
    // Callback URL (Your backend)
    public static final String CALLBACK_URL = "http://localhost:8080/api/payments/bkash/callback";
}
