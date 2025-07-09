package com.example.jobapp.service;

import com.example.jobapp.entity.JobPost;
import com.example.jobapp.model.CandidateDTO;
import com.example.jobapp.model.JobPostDTOCandidate;
import com.example.jobapp.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jobapp.entity.Candidate;
import com.example.jobapp.repository.CandidateRepository;

import java.util.ArrayList;
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

        JobPost jobPost = jobRepository.findById(jobPostId).orElse(null);

        if(jobPost == null) {
            return "No such Job Post Id " + jobPostId;
        }

        jobPost.getCandidateList().add(username);
        jobRepository.save(jobPost);

        Candidate candidate = candidateRepository.findByEmail(username);
        candidate.getJobPostList().add(jobPostId);
        candidateRepository.save(candidate);

        return "Successfully applied for " + jobPost.getPostProfile() + " role!";
    }

    public CandidateDTO getCandidateProfile() {
        CandidateDTO candidateDTO = new CandidateDTO();

        Candidate candidate = candidateRepository.findByEmail(username);
        candidateDTO.setName(candidate.getName());
        candidateDTO.setCompany(candidate.getCompany());
        candidateDTO.setEmail(candidate.getEmail());
        candidateDTO.setPassword(candidate.getPassword());
        candidateDTO.setExperience(candidate.getExperience());
        List<JobPostDTOCandidate> list = new ArrayList<>();

        for(int jobid : candidate.getJobPostList()) {
            list.add(getJobPostDTOCandidate(jobid));
        }

        candidateDTO.setJobPostList(list);

        return candidateDTO;
    }

    private JobPostDTOCandidate getJobPostDTOCandidate(int jobid) {
        JobPost job = jobRepository.findById(jobid).orElse(new JobPost());
        JobPostDTOCandidate jobPostDTOCandidate = new JobPostDTOCandidate();

        jobPostDTOCandidate.setPostProfile(job.getPostProfile());
        jobPostDTOCandidate.setPostId(job.getPostId());
        jobPostDTOCandidate.setPostDesc(job.getPostDesc());
        jobPostDTOCandidate.setLocation(job.getLocation());
        jobPostDTOCandidate.setPostTechStack(job.getPostTechStack());
        jobPostDTOCandidate.setSalary(job.getSalary());
        jobPostDTOCandidate.setCompany(job.getCompany());
        jobPostDTOCandidate.setReqExperience(job.getReqExperience());

        return jobPostDTOCandidate;
    }

    public JobPostDTOCandidate withdrawJobApplication(Integer jobPostId) {

        System.out.println("--> " + jobPostId);
        Candidate candidate = candidateRepository.findByEmail(username);

        if(!candidate.getJobPostList().contains(jobPostId)) {
            return null;
        }

        candidate.getJobPostList().remove(jobPostId);
        candidateRepository.save(candidate);

        return getJobPostDTOCandidate(jobPostId);
    }
}

