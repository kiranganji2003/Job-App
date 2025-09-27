package com.jobportal.app.controller;

import com.jobportal.app.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> login(@RequestBody LoginInfo loginInfo) {
        return ResponseEntity.ok(candidateService.candidateLoginService(loginInfo));
    }

    @PostMapping("register")
    public ResponseEntity<String> registerCandidate(@RequestBody CandidateRequestDto candidate) {
        return ResponseEntity.ok(candidateService.registerCandidate(candidate));
    }

    @GetMapping("jobpost")
    public ResponseEntity<List<JobPostDtoCandidate>> getAllJobPost() {
        return ResponseEntity.ok(candidateService.getAllJobPost());
    }

    @GetMapping(value = "jobpost", params = {"page", "size"})
    public ResponseEntity<List<JobPostDtoCandidate>> getJobsByPage(@RequestParam int page,
                                                                   @RequestParam int size) {
        return ResponseEntity.ok(candidateService.getJobsByPage(page, size));
    }

    @GetMapping(value = "jobpost", params = "search")
    public ResponseEntity<List<JobPostDtoCandidate>> getJobsByQuery(@RequestParam String search) {
        return ResponseEntity.ok(candidateService.getJobsByQuery(search));
    }

    @PostMapping("apply")
    public ResponseEntity<String> applyJobPost(@RequestBody JobPostIdDto jobPostIdDTO) {
        return ResponseEntity.ok(candidateService.applyJobPost(jobPostIdDTO.getJobPostId()));
    }

    @GetMapping("profile")
    public ResponseEntity<CandidateDto> getCandidateProfile() {
        return ResponseEntity.ok(candidateService.getCandidateProfile());
    }

    @PostMapping("withdrawn")
    public ResponseEntity<JobPostDtoCandidate> withdrawJobApplication(@RequestBody JobPostIdDto jobPostIdDTO) {
        return ResponseEntity.ok(candidateService.withdrawJobApplication(jobPostIdDTO.getJobPostId()));
    }

    @DeleteMapping("delete")
    public ResponseEntity<String> deleteCandidate() {
        return ResponseEntity.ok(candidateService.deleteCandidate());
    }

    @GetMapping("jobpost/salary")
    public ResponseEntity<List<JobPostDtoCandidate>> getJobPostBySalary(@RequestParam(required = false, defaultValue = "0") Long min, @RequestParam(required = false, defaultValue = "9223372036854775807") Long max) {
        return ResponseEntity.ok(candidateService.getJobPostBySalary(min, max));
    }

    @GetMapping(value = "jobpost", params = "techstack")
    public ResponseEntity<List<JobPostDtoCandidate>> getJobsByTechStack(@RequestParam List<String> techstack) {
        return ResponseEntity.ok(candidateService.getJobsByTechStack(techstack));
    }

    @GetMapping(value = "jobpost", params = "days")
    public ResponseEntity<List<JobPostDtoCandidate>> getJobsByLastNDays(@RequestParam Integer days) {
        return ResponseEntity.ok(candidateService.getJobsByLastNDays(days));
    }

}
