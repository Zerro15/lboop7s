package com.example.lab5.framework.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateUserRequest {
    private String login;
    private String password;
    private String role;

    public CreateUserRequest() {}

}