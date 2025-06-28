package com.example.jobapp.model;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class JobPostDTOCompany {
    private Integer postId;
    private String postProfile;
    private String postDesc;
    private String location;
    private Integer reqExperience;
    private double salary;
    private List<String> postTechStack;
    private Set<String> candidateList;
}

