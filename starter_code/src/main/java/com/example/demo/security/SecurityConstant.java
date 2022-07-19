package com.example.demo.security;

public class SecurityConstant {

    public static final String SECRET = "jwtsecretkey";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";


    public static final String CREATE_USER_URL = "/api/user/create";
    public static final long EXPIRATION_TIME = 864_000_000;
}
