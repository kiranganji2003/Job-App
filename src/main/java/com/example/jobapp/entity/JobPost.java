package com.example.jobapp.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private List<String> postTechStack;
    private Set<Integer> candidateList = new HashSet<>();

}

