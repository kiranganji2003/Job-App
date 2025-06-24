package com.example.jobapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jobapp.entity.Candidate;
import com.example.jobapp.repository.CandidateRepository;

@Service
public class CandidateService {

    private CandidateRepository candidateRepository;
    public static String username;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
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

    public String applyJobPost() {
        // TODO Auto-generated method stub

//        if(candidateRepository.findById(username).orElse(null) == null) {
//            return "fail";
//        }

        return "success";
    }

}

