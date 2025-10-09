package com.jobportal.app.controller;

import java.util.List;

import com.jobportal.app.model.*;
import com.jobportal.app.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> loginCompany(@RequestBody LoginInfo loginInfo) {
        return ResponseEntity.ok(companyService.loginCompany(loginInfo));
    }

    @PostMapping("register")
    public ResponseEntity<Message> registerCompany(@RequestBody CompanyRequestDto company) {
        return ResponseEntity.ok(companyService.registerCompany(company));
    }

    @GetMapping("profile")
    public ResponseEntity<CompanyDto> getCompanyByUsername() {
        return ResponseEntity.ok(companyService.getCompanyByUsername());
    }

    @PostMapping("jobpost")
    public ResponseEntity<Message> createJobPost(@RequestBody JobPostRequestDto jobPost) {
        return ResponseEntity.ok(companyService.createJobPost(jobPost));
    }

    @PutMapping("jobpost")
    public ResponseEntity<Message> updateJobPost(@RequestBody JobPostUpdateDto jobPost) {
        return ResponseEntity.ok(companyService.updateJobPost(jobPost));
    }

    @DeleteMapping("jobpost")
    public ResponseEntity<Message> deleteJobPost(@RequestBody JobPostIdDto jobPostIdDTO) {
        return ResponseEntity.ok(companyService.deleteJobPost(jobPostIdDTO.getJobPostId()));
    }

    @GetMapping("jobpost/insight")
    public ResponseEntity<List<JobPostInsight>> getJobpostInsight() {
        return ResponseEntity.ok(companyService.getJobpostInsight());
    }

    @PostMapping("jobpost/archive")
    public ResponseEntity<Message> archiveJobPost(@RequestBody JobPostIdDto jobPostIdDto) {
        return ResponseEntity.ok(companyService.archiveJobPost(jobPostIdDto.getJobPostId()));
    }
}