package com.example.jobapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jobapp.entity.Candidate;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, String> {
    Candidate findByEmail(String email);
}

