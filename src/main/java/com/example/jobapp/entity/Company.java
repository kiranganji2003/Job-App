package com.example.jobapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.*;

@Entity
@Data
public class Company {

    @Id
    private String username;
    private String password;
    private String name;
    private String description;
    private List<Integer> jobPostList = new ArrayList<>();

}

