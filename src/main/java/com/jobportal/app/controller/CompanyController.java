package com.jobportal.app.controller;

import java.util.List;

import com.jobportal.app.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.app.service.CompanyService;



@RestController
@RequestMapping("company")
public class CompanyController {

    private CompanyService companyService;


    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("sessions")
    String loginCompany(@RequestBody LoginInfo loginInfo) {
        return companyService.loginCompany(loginInfo);
    }

    @PostMapping("register")
    String registerCompany(@RequestBody CompanyRequestDto company) {
        return companyService.registerCompany(company);
    }

    @GetMapping("profile")
    CompanyDto getCompanyByUsername() {
        return companyService.getCompanyByUsername();
    }

    @PostMapping("jobpost")
    String createJobPost(@RequestBody JobPostRequestDto jobPost) {
        return companyService.createJobPost(jobPost);
    }

    @PutMapping("jobpost")
    String updateJobPost(@RequestBody JobPostUpdateDto jobPost) {
        return companyService.updateJobPost(jobPost);
    }

    @DeleteMapping("jobpost")
    String deleteJobPost(@RequestBody JobPostIdDto jobPostIdDTO) {
        return companyService.deleteJobPost(jobPostIdDTO.getJobPostId());
    }

    @GetMapping("jobpost/insight")
    List<JobPostInsight> getJobpostInsight() {
        return companyService.getJobpostInsight();
    }
}
