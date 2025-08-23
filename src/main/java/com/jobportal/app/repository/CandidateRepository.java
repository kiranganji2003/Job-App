package com.jobportal.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jobportal.app.entity.Candidate;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, String> {
    Candidate findByEmail(String email);
}

