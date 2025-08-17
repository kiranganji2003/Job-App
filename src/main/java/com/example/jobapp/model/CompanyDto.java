package com.example.jobapp.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompanyDto {

    private String username;
    private String password;
    private String name;
    private String description;
    private List<JobPostDtoCompany> jobPostList = new ArrayList<>();
}

