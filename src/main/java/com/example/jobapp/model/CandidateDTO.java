package com.example.jobapp.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CandidateDTO {
    private String email;
    private String password;
    private String name;
    private Integer experience;
    private String company;
    private List<JobPostDTOCandidate> jobPostList = new ArrayList<>();
}
