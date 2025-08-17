package com.example.jobapp.model;

import lombok.Data;

@Data
public class CompanyRequestDto {
    private String username;
    private String password;
    private String name;
    private String description;
}
