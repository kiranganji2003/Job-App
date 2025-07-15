package com.example.jobapp.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class JobPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;
    private String postProfile;
    private String company;
    private String postDesc;
    private String location;
    private Integer reqExperience;
    private double salary;
    @ElementCollection
    private List<String> postTechStack = new ArrayList<>();
    @ElementCollection
    private Set<String> candidateList = new HashSet<>();

}

