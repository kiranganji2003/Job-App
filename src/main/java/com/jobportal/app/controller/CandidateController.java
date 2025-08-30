package com.jobportal.app.controller;

import com.jobportal.app.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jobportal.app.service.CandidateService;

import java.util.List;

@RestController
@RequestMapping("candidate")
public class CandidateController {

    private CandidateService candidateService;
    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    @Autowired
    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping("login")
    public String login(@RequestBody LoginInfo loginInfo) {
        return candidateService.candidateLoginService(loginInfo);
    }

    @PostMapping("register")
    String registerCandidate(@RequestBody CandidateRequestDto candidate) {
        return candidateService.registerCandidate(candidate);
    }

    @GetMapping("jobpost")
    List<JobPostDtoCandidate> getAllJobPost() {
        logger.info("getAllJobPost() called");
        List<JobPostDtoCandidate> jobs = candidateService.getAllJobPost();
        logger.info("getAllJobPost() returned {} job posts", jobs.size());
        return jobs;
    }

    @GetMapping("jobpost/search")
    List<JobPostDtoCandidate> getJobsByQuery(@RequestParam String query) {
        logger.info("getJobsByQuery() started with query='{}'", query);
        List<JobPostDtoCandidate> jobs = candidateService.getJobsByQuery(query);
        logger.info("getJobsByQuery() found {} results for query='{}'", jobs.size(), query);
        return jobs;
    }

    @PostMapping("apply")
    String applyJobPost(@RequestBody JobPostIdDto jobPostIdDTO) {
        logger.info("applyJobPost() started for jobPostId={}", jobPostIdDTO.getJobPostId());
        return candidateService.applyJobPost(jobPostIdDTO.getJobPostId());
    }

    @GetMapping("profile")
    CandidateDto getCandidateProfile() {
        logger.info("getCandidateProfile() started");
        return candidateService.getCandidateProfile();
    }

    @PostMapping("withdrawn")
    JobPostDtoCandidate withdrawJobApplication(@RequestBody JobPostIdDto jobPostIdDTO) {
        return candidateService.withdrawJobApplication(jobPostIdDTO.getJobPostId());
    }

    @DeleteMapping("delete")
    String deleteCandidate() {
        logger.info("deleteCandidate() called");
        String response = candidateService.deleteCandidate();
        logger.info("deleteCandidate() response: {}", response);
        return response;
    }

    @GetMapping("jobpost/salary")
    List<JobPostDtoCandidate> getJobPostBySalary(@RequestParam(required = false) Long min, @RequestParam(required = false) Long max) {
        min = (min == null ? 0 : min);
        max = (max == null ? Long.MAX_VALUE : max);

        return candidateService.getJobPostBySalary(min, max);
    }

    @GetMapping("jobpost/search/techstack")
    List<JobPostDtoCandidate> getJobsByTechStack(@RequestBody TechStackList techStackList) {
        return candidateService.getJobsByTechStack(techStackList.getTechStackList());
    }

}
