package com.hcmute.shopfee.constant;


public class SecurityConstant {
    public static final String ROLE_ADMIN = "hasRole('ROLE_ADMIN')";
    public static final String ROLE_MANAGER = "hasRole('ROLE_MANAGER')";
    public static final String ROLE_USER = "hasRole('ROLE_USER')";
    public static final String ROLE_WAITER = "hasRole('ROLE_WAITER')";
    public static final String ROLE_SHIPPER = "hasRole('ROLE_SHIPPER')";
    public static final String ROLE_ADMIN_MANAGER_WAITER = "hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_WAITER')";
    public static final String ROLE_ADMIN_WAITER_USER = "hasAnyRole('ROLE_ADMIN', 'ROLE_WAITER', 'ROLE_USER')";
    public static final String ROLE_ADMIN_MANAGER = "hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')";
    public static final String ROLE_WAITER_USER = "hasAnyRole('ROLE_WAITER', 'ROLE_USER')";


    // ALL =================================================================
    public static final String[] GET_AUTH_WHITELIST = {
            "/api/payment/**",
            "/IPN/**",
            "/openapi/**", "/v3/api-docs/**", "/openapi/swagger-config/**",
            "/v3/api-docs.yaml", "/swagger-ui/**", "/swagger-ui.html", "/tool/**",
    };
    public static final String[] POST_AUTH_WHITELIST = {
            "/refund",
            "/IPN/**",
    };


}
