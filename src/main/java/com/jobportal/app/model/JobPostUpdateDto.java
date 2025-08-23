package com.jobportal.app.model;

import jakarta.persistence.ElementCollection;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class JobPostUpdateDto {
    private Integer postId;
    private String postProfile;
    private String postDesc;
    private String location;
    private Integer reqExperience;
    private double salary;
    private List<String> postTechStack = new ArrayList<>();
}
