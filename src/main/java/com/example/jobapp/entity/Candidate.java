package com.example.jobapp.entity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Candidate {

    @Id
    private String email;
    private String password;
    private String name;
    private Integer experience;
    private String company;

    @ElementCollection
    Map<Integer, LocalDate> jobPostListDate = new HashMap<>();
}

