package com.example.jobapp.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.jobapp.entity.Candidate;
import com.example.jobapp.exception.AlreadyRegisteredException;
import com.example.jobapp.exception.InvalidCredentialsException;
import com.example.jobapp.model.*;
import com.example.jobapp.repository.CandidateRepository;
import com.example.jobapp.security.JwtUtilCompany;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jobapp.entity.Company;
import com.example.jobapp.entity.JobPost;
import com.example.jobapp.repository.CompanyRepository;
import com.example.jobapp.repository.JobRepository;

@Service
public class JobService {

    private CompanyRepository companyRepository;
    private JobRepository jobRepository;
    private CandidateRepository candidateRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtilCompany jwtUtilCompany;
    public static String username;


    @Autowired
    public JobService(CompanyRepository companyRepository, JobRepository jobRepository, CandidateRepository candidateRepository, PasswordEncoder passwordEncoder, JwtUtilCompany jwtUtilCompany) {
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.candidateRepository = candidateRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtilCompany = jwtUtilCompany;
    }


    public String registerCompany(CompanyRequestDto company) {

        if(companyRepository.findByUsername(company.getUsername()) != null) {
            throw new AlreadyRegisteredException("Username " + company.getUsername() + " already registered");
        }

        company.setPassword(passwordEncoder.encode(company.getPassword()));
        companyRepository.save(convertToCompanyEntity(company));
        return "Registered Successfully";
    }

    private Company convertToCompanyEntity(CompanyRequestDto companyRequestDto) {
        Company company = new Company();

        company.setUsername(companyRequestDto.getUsername());
        company.setPassword(companyRequestDto.getPassword());
        company.setName(companyRequestDto.getName());
        company.setDescription(companyRequestDto.getDescription());

        return company;
    }


    public CompanyDto getCompanyByUsername() {
        // TODO Auto-generated method stub
        Company company = companyRepository.findByUsername(username);
        CompanyDto companyDTO = new CompanyDto();

        companyDTO.setUsername(company.getUsername());
        companyDTO.setPassword(company.getPassword());
        companyDTO.setName(company.getName());
        companyDTO.setDescription(company.getDescription());

        for(int jobPostId : company.getJobPostListAndDate().keySet()) {
            JobPostDtoCompany jobPostDTOCompany = convertToJobPostDtoCompany(jobRepository.findById(jobPostId).orElse(null));
            jobPostDTOCompany.setJobPostDate(company.getJobPostListAndDate().get(jobPostId));
            companyDTO.getJobPostList().add(jobPostDTOCompany);
        }

        return companyDTO;
    }



    private JobPostDtoCompany convertToJobPostDtoCompany(JobPost jobPost) {
        // TODO Auto-generated method stub
        JobPostDtoCompany jobPostDTOCompany = new JobPostDtoCompany();
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



    public String createJobPost(JobPostRequestDto jobPostRequestDto) {
        // TODO Auto-generated method stub

        Company company = companyRepository.getReferenceById(username);
        JobPost jobPost = convertToJobPostEntity(jobPostRequestDto);
        jobPost.setCompany(company.getName());
        jobPost.setCompanyUsername(username);
        JobPost savedJobPost = jobRepository.save(jobPost);
        company.getJobPostListAndDate().put(savedJobPost.getPostId(), LocalDate.now());
        companyRepository.save(company);

        return "Job Posted Successfully";
    }

    private JobPost convertToJobPostEntity(JobPostRequestDto jobPostRequestDto) {
        JobPost jobPost = new JobPost();
        jobPost.setPostProfile(jobPostRequestDto.getPostProfile());
        jobPost.setPostDesc(jobPostRequestDto.getPostDesc());
        jobPost.setLocation(jobPostRequestDto.getLocation());
        jobPost.setReqExperience(jobPostRequestDto.getReqExperience());
        jobPost.setSalary(jobPostRequestDto.getSalary());
        jobPost.setPostTechStack(jobPostRequestDto.getPostTechStack());
        return jobPost;
    }

    private boolean companyContainsJobPost(int jobpost) {
        Company company = companyRepository.findByUsername(username);
        return company.getJobPostListAndDate().keySet().contains(jobpost);
    }


    public ResponseEntity<String> updateJobPost(JobPostUpdateDto jobPost) {
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

    public String loginCompany(LoginInfo loginInfo) {
        String username = loginInfo.getUsername();
        String password = loginInfo.getPassword();

        Company userOpt = companyRepository.findByUsername(username);

        if (userOpt == null || !passwordEncoder.matches(password, userOpt.getPassword())) {
            throw new InvalidCredentialsException("Invalid Company Credentials");
        }

        return jwtUtilCompany.generateToken(username);
    }
}

