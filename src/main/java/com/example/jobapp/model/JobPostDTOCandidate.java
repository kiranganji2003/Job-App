package com.example.jobapp.model;

import lombok.Data;

import java.util.List;

@Data
public class JobPostDTOCandidate {
    private Integer postId;
    private String postProfile;
    private String company;
    private String postDesc;
    private String location;
    private Integer reqExperience;
    private double salary;
    private List<String> postTechStack;
}
