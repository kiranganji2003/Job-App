package com.jobportal.app.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jobportal.app.entity.Candidate;
import com.jobportal.app.exception.AlreadyRegisteredException;
import com.jobportal.app.exception.InvalidCredentialsException;
import com.jobportal.app.exception.InvalidJobPostIdException;
import com.jobportal.app.model.*;
import com.jobportal.app.repository.CandidateRepository;
import com.jobportal.app.security.JwtUtilCompany;
import com.jobportal.app.utility.AppMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobportal.app.entity.Company;
import com.jobportal.app.entity.JobPost;
import com.jobportal.app.repository.CompanyRepository;
import com.jobportal.app.repository.JobRepository;

@Service
public class CompanyService {

    private CompanyRepository companyRepository;
    private JobRepository jobRepository;
    private CandidateRepository candidateRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtilCompany jwtUtilCompany;

    @Autowired
    public CompanyService(CompanyRepository companyRepository, JobRepository jobRepository, CandidateRepository candidateRepository, PasswordEncoder passwordEncoder, JwtUtilCompany jwtUtilCompany) {
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.candidateRepository = candidateRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtilCompany = jwtUtilCompany;
    }


    private String getLoggedInUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public String registerCompany(CompanyRequestDto company) {

        if(companyRepository.findByUsername(company.getUsername()) != null) {
            throw new AlreadyRegisteredException(String.format(AppMessages.USERNAME_ALREADY_REGISTERED, company.getUsername()));
        }

        company.setPassword(passwordEncoder.encode(company.getPassword()));
        companyRepository.save(convertToCompanyEntity(company));
        return AppMessages.REGISTERED_SUCCESSFULLY;
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
        Company company = companyRepository.findByUsername(getLoggedInUsername());
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

        Company company = companyRepository.getReferenceById(getLoggedInUsername());
        JobPost jobPost = convertToJobPostEntity(jobPostRequestDto);
        jobPost.setCompany(company.getName());
        jobPost.setCompanyUsername(getLoggedInUsername());
        JobPost savedJobPost = jobRepository.save(jobPost);
        company.getJobPostListAndDate().put(savedJobPost.getPostId(), LocalDate.now());
        companyRepository.save(company);

        return AppMessages.JOB_POSTED_SUCCESSFULLY;
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
        Company company = companyRepository.findByUsername(getLoggedInUsername());
        return company.getJobPostListAndDate().keySet().contains(jobpost);
    }


    public String updateJobPost(JobPostUpdateDto jobPost) {

        if(!companyContainsJobPost(jobPost.getPostId())) {
            throw new InvalidJobPostIdException(String.format(AppMessages.NO_SUCH_JOB_POST, jobPost.getPostId()));
        }

        JobPost job = jobRepository.getReferenceById(jobPost.getPostId());
        job.setPostProfile(jobPost.getPostProfile());
        job.setPostDesc(jobPost.getPostDesc());
        job.setLocation(jobPost.getLocation());
        job.setReqExperience(jobPost.getReqExperience());
        job.setSalary(jobPost.getSalary());
        job.setPostTechStack(jobPost.getPostTechStack());

        jobRepository.save(job);

        return AppMessages.JOB_POST_UPDATED_SUCCESSFULLY;
    }



    public String deleteJobPost(Integer postId) {

        if(!companyContainsJobPost(postId)) {
            throw new InvalidJobPostIdException(String.format(AppMessages.NOT_VALID_JOB_POST, postId));
        }

        Company company = companyRepository.findByUsername(getLoggedInUsername());
        company.getJobPostListAndDate().remove(Integer.valueOf(postId));
        companyRepository.save(company);

        JobPost jobPost = jobRepository.findById(postId).orElse(new JobPost());

        for(String candidateEmail : jobPost.getCandidateList()) {
            Candidate candidate = candidateRepository.findByEmail(candidateEmail);
            candidate.getJobPostListAndDate().remove(postId);
            candidateRepository.save(candidate);
        }

        jobRepository.deleteById(postId);

        return AppMessages.JOB_POST_DELETED_SUCCESSFULLY;
    }

    public List<JobPostInsight> getJobpostInsight() {
        List<JobPostInsight> jobPostList = new ArrayList<>();
        Company company = companyRepository.findByUsername(getLoggedInUsername());

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
            throw new InvalidCredentialsException(AppMessages.INVALID_COMPANY_CREDENTIALS);
        }

        return jwtUtilCompany.generateToken(username);
    }
}

