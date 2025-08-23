package com.example.jobapp.controller;

import java.util.List;

import com.example.jobapp.model.*;
import com.example.jobapp.repository.CompanyRepository;
import com.example.jobapp.security.JwtUtilCompany;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobapp.entity.Company;
import com.example.jobapp.service.JobService;



@RestController
@RequestMapping("company")
public class JobController {

    private JobService jobService;


    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("login")
    String loginCompany(@RequestBody LoginInfo loginInfo) {
        return jobService.loginCompany(loginInfo);
    }

    @PostMapping("register")
    String registerCompany(@RequestBody CompanyRequestDto company) {
        return jobService.registerCompany(company);
    }

    @GetMapping("view")
    CompanyDto getCompanyByUsername() {
        return jobService.getCompanyByUsername();
    }

    @PostMapping("jobpost")
    String createJobPost(@RequestBody JobPostRequestDto jobPost) {
        return jobService.createJobPost(jobPost);
    }

    @PutMapping("jobpost")
    ResponseEntity<String> updateJobPost(@RequestBody JobPostUpdateDto jobPost) {
        return jobService.updateJobPost(jobPost);
    }

    @DeleteMapping("jobpost")
    ResponseEntity<String> deleteJobPost(@RequestBody JobPostIdDto jobPostIdDTO) {
        return jobService.deleteJobPost(jobPostIdDTO.getJobPostId());
    }

    @GetMapping("jobpost/insight")
    List<JobPostInsight> getJobpostInsight() {
        return jobService.getJobpostInsight();
    }
}
