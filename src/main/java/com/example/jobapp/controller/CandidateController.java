package com.example.jobapp.controller;

import com.example.jobapp.entity.JobPost;
import com.example.jobapp.repository.CandidateRepository;
import com.example.jobapp.security.JwtUtilCandidate;
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

    @Autowired
    public CandidateController(CandidateService candidateService, CandidateRepository candidateRepository, JwtUtilCandidate jwtUtil, PasswordEncoder passwordEncoder) {
        this.candidateService = candidateService;
        this.candidateRepository = candidateRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        Candidate userOpt = candidateRepository.findByEmail(username);
        if (userOpt != null && passwordEncoder.matches(password, userOpt.getPassword())) {
            String token = jwtUtil.generateToken(username);
            return new ResponseEntity<>(token, HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("register")
    ResponseEntity<String> registerCandidate(@RequestBody Candidate candidate) {
        String res = candidateService.registerCandidate(candidate);

        if(res == null) {
            return new ResponseEntity<String>("Email " + candidate.getEmail() + " already registered", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<String>(res, HttpStatus.OK);
    }

    @GetMapping("jobpost")
    List<JobPost> getAllJobPost() {
        return candidateService.getAllJobPost();
    }

    @GetMapping("jobpost/search")
    List<JobPost> getJobsByQuery(@RequestParam String query) {
        return candidateService.getJobsByQuery(query);
    }

    @PostMapping("apply")
    String applyJobPost(@RequestBody Map<String, Integer> map) {
        return candidateService.applyJobPost(map.getOrDefault("jobPostId", -1));
    }

}

