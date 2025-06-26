package com.example.jobapp.service;

import com.example.jobapp.entity.JobPost;
import com.example.jobapp.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jobapp.entity.Candidate;
import com.example.jobapp.repository.CandidateRepository;

import java.util.List;

@Service
public class CandidateService {

    private CandidateRepository candidateRepository;
    private JobRepository jobRepository;
    public static String username;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository, JobRepository jobRepository) {
        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
    }


    public String registerCandidate(Candidate candidate) {
        // TODO Auto-generated method stub

        if(candidateRepository.findById(candidate.getEmail()).orElse(null) == null) {
            candidate.setPassword(new BCryptPasswordEncoder().encode(candidate.getPassword()));
            candidateRepository.save(candidate);
            return "Registered Successfully";
        }

        return null;
    }

    public List<JobPost> getAllJobPost() {
        // TODO Auto-generated method stub
        return jobRepository.findAll();
    }

    public List<JobPost> getJobsByQuery(String str) {
        // TODO Auto-generated method stub
        return jobRepository.searchJob(str);
    }

    public String applyJobPost(Integer jobPostId) {
        // TODO Auto-generated method stub

        if(jobRepository.findById(jobPostId).orElse(null) == null) {
            return "No such Job Post Id " + jobPostId;
        }

        return "success";
    }

}

