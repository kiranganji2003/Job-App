package com.jobportal.app.model;

import lombok.Data;

@Data
public class CandidateRequestDto {
    private String email;
    private String password;
    private String name;
    private Integer experience;
    private String company;
}
