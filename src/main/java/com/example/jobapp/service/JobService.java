package com.example.jobapp.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.jobapp.entity.Candidate;
import com.example.jobapp.model.JobPostInsight;
import com.example.jobapp.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jobapp.entity.Company;
import com.example.jobapp.entity.JobPost;
import com.example.jobapp.model.CompanyDTO;
import com.example.jobapp.model.JobPostDTOCompany;
import com.example.jobapp.repository.CompanyRepository;
import com.example.jobapp.repository.JobRepository;

@Service
public class JobService {

    private CompanyRepository companyRepository;
    private JobRepository jobRepository;
    private CandidateRepository candidateRepository;
    private PasswordEncoder passwordEncoder;
    public static String username;


    @Autowired
    public JobService(CompanyRepository companyRepository, JobRepository jobRepository, CandidateRepository candidateRepository, PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.candidateRepository = candidateRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public String registerCompany(Company company) {
        // TODO Auto-generated method stub
        if(companyRepository.findByUsername(company.getUsername()) != null) {
            return "Username already exists";
        }

        company.setPassword(passwordEncoder.encode(company.getPassword()));
        companyRepository.save(company);
        return "Registered Successfully";
    }



    public CompanyDTO getCompanyByUsername() {
        // TODO Auto-generated method stub
        Company company = companyRepository.findByUsername(username);
        CompanyDTO companyDTO = new CompanyDTO();

        companyDTO.setUsername(company.getUsername());
        companyDTO.setPassword(company.getPassword());
        companyDTO.setName(company.getName());
        companyDTO.setDescription(company.getDescription());

        for(int jobPostId : company.getJobPostListAndDate().keySet()) {
            JobPostDTOCompany jobPostDTOCompany = convertToJobPostDtoCompany(jobRepository.findById(jobPostId).orElse(null));
            jobPostDTOCompany.setJobPostDate(company.getJobPostListAndDate().get(jobPostId));
            companyDTO.getJobPostList().add(jobPostDTOCompany);
        }

        return companyDTO;
    }



    private JobPostDTOCompany convertToJobPostDtoCompany(JobPost jobPost) {
        // TODO Auto-generated method stub
        JobPostDTOCompany jobPostDTOCompany = new JobPostDTOCompany();
        jobPostDTOCompany.setPostId(jobPost.getPostId());
        jobPostDTOCompany.setPostProfile(jobPost.getPostProfile());
        jobPostDTOCompany.setPostDesc(jobPost.getPostDesc());
        jobPostDTOCompany.setLocation(jobPost.getLocation());
        jobPostDTOCompany.setReqExperience(jobPost.getReqExperience());
        jobPostDTOCompany.setSalary(jobPost.getSalary());
        jobPostDTOCompany.setPostTechStack(jobPost.getPostTechStack());
        jobPostDTOCompany.setCandidateList(jobPost.getCandidateList());
        return jobPostDTOCompany;
    }



    public String createJobPost(JobPost jobPost) {
        // TODO Auto-generated method stub

        Company company = companyRepository.getReferenceById(username);
        jobPost.setCompany(company.getName());
        JobPost savedJobPost = jobRepository.save(jobPost);
        company.getJobPostListAndDate().put(savedJobPost.getPostId(), LocalDate.now());
        companyRepository.save(company);

        return "Job Posted Successfully";
    }

    private boolean companyContainsJobPost(int jobpost) {
        Company company = companyRepository.findByUsername(username);
        return company.getJobPostListAndDate().keySet().contains(jobpost);
    }


    public ResponseEntity<String> updateJobPost(JobPost jobPost) {
        // TODO Auto-generated method stub

        if(!companyContainsJobPost(jobPost.getPostId())) {
            return new ResponseEntity<String>("Not valid job post id", HttpStatus.BAD_REQUEST);
        }

        JobPost job = jobRepository.getReferenceById(jobPost.getPostId());
        job.setPostProfile(jobPost.getPostProfile());
        job.setPostDesc(jobPost.getPostDesc());
        job.setLocation(jobPost.getLocation());
        job.setReqExperience(jobPost.getReqExperience());
        job.setSalary(jobPost.getSalary());
        job.setPostTechStack(jobPost.getPostTechStack());

        jobRepository.save(job);

        return new ResponseEntity<String>("Job Post Updated Successfully", HttpStatus.OK);
    }



    public ResponseEntity<String> deleteJobPost(Integer postId) {
        // TODO Auto-generated method stub

        if(!companyContainsJobPost(postId)) {
            return new ResponseEntity<String>("Not valid job post id", HttpStatus.NOT_FOUND);
        }

        Company company = companyRepository.findByUsername(username);
        company.getJobPostListAndDate().remove(Integer.valueOf(postId));
        companyRepository.save(company);

        JobPost jobPost = jobRepository.findById(postId).orElse(new JobPost());

        for(String candidateEmail : jobPost.getCandidateList()) {
            Candidate candidate = candidateRepository.findByEmail(candidateEmail);
            candidate.getJobPostListAndDate().remove(postId);
            candidateRepository.save(candidate);
        }

        jobRepository.deleteById(postId);

        return new ResponseEntity<String>("Job Post Deleted Succesfully", HttpStatus.OK);
    }

    public List<JobPostInsight> getJobpostInsight() {
        List<JobPostInsight> jobPostList = new ArrayList<>();
        Company company = companyRepository.findByUsername(username);

        for(int postId : company.getJobPostListAndDate().keySet()) {
            JobPost jobPost = jobRepository.findById(postId).orElse(new JobPost());
            JobPostInsight jobPostInsight = new JobPostInsight();
            jobPostInsight.setPostId(postId);
            jobPostInsight.setJobPostDate(company.getJobPostListAndDate().get(postId));
            jobPostInsight.setCandidatesApplied(jobPost.getCandidateList().size());
            jobPostInsight.setCandidateList(jobPost.getCandidateList());
            jobPostList.add(jobPostInsight);
        }

        Collections.sort(jobPostList);

        return jobPostList;
    }
}

