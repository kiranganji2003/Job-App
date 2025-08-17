package com.example.jobapp.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JobPostRequestDto {
    private String postProfile;
    private String postDesc;
    private String location;
    private Integer reqExperience;
    private double salary;
    private List<String> postTechStack = new ArrayList<>();
}
