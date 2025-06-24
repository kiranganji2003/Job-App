package com.example.jobapp.controller;

import java.util.List;
import java.util.Map;

import com.example.jobapp.repository.CompanyRepository;
import com.example.jobapp.security.JwtUtilCompany;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobapp.entity.Company;
import com.example.jobapp.entity.JobPost;
import com.example.jobapp.model.CompanyDTO;
import com.example.jobapp.service.JobService;



@RestController
@RequestMapping("company")
public class JobController {

    private JobService jobService;
    private CompanyRepository companyRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtilCompany jwtUtilCompany;

    @Autowired
    public JobController(JobService jobService, CompanyRepository companyRepository, PasswordEncoder passwordEncoder, JwtUtilCompany jwtUtilCompany) {
        this.jobService = jobService;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtilCompany = jwtUtilCompany;
    }

    @PostMapping("login")
    ResponseEntity<String> loginCompany(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        Company userOpt = companyRepository.findByUsername(username);

        System.out.println(userOpt != null && passwordEncoder.matches(password, userOpt.getPassword()));
        System.out.println(userOpt.getPassword());
        System.out.println(passwordEncoder.encode(password));

        if (userOpt != null && passwordEncoder.matches(password, userOpt.getPassword())) {
            System.out.println("hii.... ");
            String token = jwtUtilCompany.generateToken(username);
            return new ResponseEntity<>(token, HttpStatus.OK);
        }

        return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("register")
    String registerCompany(@RequestBody Company company) {
        return jobService.registerCompany(company);
    }

    @GetMapping("view")
    CompanyDTO getCompanyByUsername() {
        return jobService.getCompanyByUsername();
    }

    @PostMapping("jobpost")
    String createJobPost(@RequestBody JobPost jobPost) {
        return jobService.createJobPost(jobPost);
    }

    @GetMapping("jobpost")
    List<JobPost> getAllJobPost() {
        return jobService.getAllJobPost();
    }

    @GetMapping("jobpost/search")
    List<JobPost> getJobsByQuery(@RequestParam String query) {
        return jobService.getJobsByQuery(query);
    }

    @PutMapping("jobpost")
    ResponseEntity<String> updateJobPost(@RequestBody JobPost jobPost) {
        return jobService.updateJobPost(jobPost);
    }

    @DeleteMapping("jobpost")
    ResponseEntity<String> deleteJobPost(@RequestBody Integer jobPostId) {
        return jobService.deleteJobPost(jobPostId);
    }
}
