package com.example.jobapp.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CandidateDto {
    private String email;
    private String password;
    private String name;
    private Integer experience;
    private String company;
    private List<JobPostDtoCandidate> jobPostList = new ArrayList<>();
}
