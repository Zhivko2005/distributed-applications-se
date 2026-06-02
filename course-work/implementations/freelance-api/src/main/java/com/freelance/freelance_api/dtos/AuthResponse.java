package com.freelance.freelance_api.dtos;

public class AuthResponse {
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }
    public String getToken(){
         return token;
    }
    public void  setToken(){
        this.token=token;
    }

}
