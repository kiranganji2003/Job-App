package com.example.jobapp.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompanyDTO {

    private String username;
    private String password;
    private String name;
    private String description;
    private List<JobPostDTOCompany> jobPostList = new ArrayList<>();
}

