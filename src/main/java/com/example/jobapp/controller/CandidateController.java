package com.example.jobapp.controller;

import com.example.jobapp.model.CandidateDTO;
import com.example.jobapp.model.JobPostDTOCandidate;
import com.example.jobapp.repository.CandidateRepository;
import com.example.jobapp.security.JwtUtilCandidate;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.jobapp.entity.Candidate;
import com.example.jobapp.service.CandidateService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("candidate")
public class CandidateController {

    private CandidateService candidateService;
    private CandidateRepository candidateRepository;
    private JwtUtilCandidate jwtUtil;
    private PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(CandidateController.class);

    @Autowired
    public CandidateController(CandidateService candidateService, CandidateRepository candidateRepository, JwtUtilCandidate jwtUtil, PasswordEncoder passwordEncoder) {
        this.candidateService = candidateService;
        this.candidateRepository = candidateRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
        logger.info("login() started for username={}", request.get("username"));
        String username = request.get("username");
        String password = request.get("password");

        Candidate userOpt = candidateRepository.findByEmail(username);
        if (userOpt != null && passwordEncoder.matches(password, userOpt.getPassword())) {
            String token = jwtUtil.generateToken(username);
            logger.info("login successful for username={}", username);
            return new ResponseEntity<>(token, HttpStatus.OK);
        }

        logger.warn("login failed for username={}: invalid credentials", username);
        return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("register")
    ResponseEntity<String> registerCandidate(@RequestBody Candidate candidate) {
        logger.info("registerCandidate() started for email={}", candidate.getEmail());
        logger.debug("Candidate details: {}", candidate);

        String res = candidateService.registerCandidate(candidate);

        if(res == null) {
            logger.warn("registerCandidate() failed: email {} already registered", candidate.getEmail());
            return new ResponseEntity<String>("Email " + candidate.getEmail() + " already registered", HttpStatus.BAD_REQUEST);
        }

        logger.info("registerCandidate() succeeded for email={}", candidate.getEmail());
        return new ResponseEntity<String>(res, HttpStatus.OK);
    }

    @GetMapping("jobpost")
    List<JobPostDTOCandidate> getAllJobPost() {
        logger.info("getAllJobPost() called");
        List<JobPostDTOCandidate> jobs = candidateService.getAllJobPost();
        logger.info("getAllJobPost() returned {} job posts", jobs.size());
        return jobs;
    }

    @GetMapping("jobpost/search")
    List<JobPostDTOCandidate> getJobsByQuery(@RequestParam String query) {
        logger.info("getJobsByQuery() started with query='{}'", query);
        List<JobPostDTOCandidate> jobs = candidateService.getJobsByQuery(query);
        logger.info("getJobsByQuery() found {} results for query='{}'", jobs.size(), query);
        return jobs;
    }

    @PostMapping("apply")
    String applyJobPost(@RequestBody Map<String, Integer> map) {
        logger.info("applyJobPost() started for jobPostId={}", map.getOrDefault("jobPostId", -1));
        return candidateService.applyJobPost(map.getOrDefault("jobPostId", -1));
    }

    @GetMapping("profile")
    CandidateDTO getCandidateProfile() {
        logger.info("getCandidateProfile() started");
        return candidateService.getCandidateProfile();
    }

    @PostMapping("withdrawn")
    ResponseEntity<JobPostDTOCandidate> withdrawJobApplication(@RequestBody Map<String, Integer> map) {
        logger.info("withdrawJobApplication() started for jobPostId={}", map.getOrDefault("jobPostId", -1));
        JobPostDTOCandidate jobPostDTOCandidate = candidateService.withdrawJobApplication(map.getOrDefault("jobPostId", -1));

        if(jobPostDTOCandidate == null) {
            logger.warn("withdrawJobApplication() invalid jobPostId");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        logger.info("withdrawJobApplication() succeeded for jobPostId={}", map.get("jobPostId"));
        return new ResponseEntity<>(jobPostDTOCandidate, HttpStatus.OK);
    }

    @DeleteMapping("delete")
    String deleteCandidate() {
        logger.info("deleteCandidate() called");
        String response = candidateService.deleteCandidate();
        logger.info("deleteCandidate() response: {}", response);
        return response;
    }

    @GetMapping("jobpost/salary")
    List<JobPostDTOCandidate> getJobPostBySalary(@RequestParam(required = false) Long min, @RequestParam(required = false) Long max) {
        min = (min == null ? 0 : min);
        max = (max == null ? Long.MAX_VALUE : max);

        return candidateService.getJobPostBySalary(min, max);
    }

    @GetMapping("jobpost/search/techstack")
    List<JobPostDTOCandidate> getJobsByTechStack(@RequestBody TechStackList techStackList) {
        return candidateService.getJobsByTechStack(techStackList.getTechStackList());
    }

}

@Data
class TechStackList {
    List<String> techStackList;
}
