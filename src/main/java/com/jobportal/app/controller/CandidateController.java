package com.jobportal.app.controller;

import com.jobportal.app.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jobportal.app.service.CandidateService;

import java.util.List;

@RestController
@RequestMapping("candidate")
public class CandidateController {

    private CandidateService candidateService;

    @Autowired
    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping("sessions")
    public String login(@RequestBody LoginInfo loginInfo) {
        return candidateService.candidateLoginService(loginInfo);
    }

    @PostMapping("register")
    String registerCandidate(@RequestBody CandidateRequestDto candidate) {
        return candidateService.registerCandidate(candidate);
    }

    @GetMapping("jobpost")
    List<JobPostDtoCandidate> getAllJobPost() {
        return candidateService.getAllJobPost();
    }

    @GetMapping("jobpost/search")
    List<JobPostDtoCandidate> getJobsByQuery(@RequestParam String query) {
        return candidateService.getJobsByQuery(query);
    }

    @PostMapping("apply")
    String applyJobPost(@RequestBody JobPostIdDto jobPostIdDTO) {
        return candidateService.applyJobPost(jobPostIdDTO.getJobPostId());
    }

    @GetMapping("profile")
    CandidateDto getCandidateProfile() {
        return candidateService.getCandidateProfile();
    }

    @PostMapping("withdrawn")
    JobPostDtoCandidate withdrawJobApplication(@RequestBody JobPostIdDto jobPostIdDTO) {
        return candidateService.withdrawJobApplication(jobPostIdDTO.getJobPostId());
    }

    @DeleteMapping("delete")
    String deleteCandidate() {
        return candidateService.deleteCandidate();
    }

    @GetMapping("jobpost/salary")
    List<JobPostDtoCandidate> getJobPostBySalary(@RequestParam(required = false, defaultValue = "0") Long min, @RequestParam(required = false, defaultValue = "9223372036854775807") Long max) {
        return candidateService.getJobPostBySalary(min, max);
    }

    @GetMapping("jobpost/search/techstack")
    List<JobPostDtoCandidate> getJobsByTechStack(@RequestParam List<String> techStackList) {
        return candidateService.getJobsByTechStack(techStackList);
    }

}
