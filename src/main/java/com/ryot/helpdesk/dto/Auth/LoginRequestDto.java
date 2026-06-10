package com.ryot.helpdesk.dto.Auth;


import lombok.*;



@Getter
@Setter
public class LoginRequestDto {
    private String email;
    private String password;
}
