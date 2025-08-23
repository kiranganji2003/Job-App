package com.example.jobapp.service;

import com.example.jobapp.entity.Company;
import com.example.jobapp.entity.JobPost;
import com.example.jobapp.exception.InvalidCredentialsException;
import com.example.jobapp.model.CandidateDto;
import com.example.jobapp.model.CandidateRequestDto;
import com.example.jobapp.model.JobPostDtoCandidate;
import com.example.jobapp.model.LoginInfo;
import com.example.jobapp.repository.CompanyRepository;
import com.example.jobapp.repository.JobRepository;
import com.example.jobapp.security.JwtUtilCandidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jobapp.entity.Candidate;
import com.example.jobapp.repository.CandidateRepository;

import java.time.LocalDate;
import java.util.*;

@Service
public class CandidateService {

    private CandidateRepository candidateRepository;
    private JobRepository jobRepository;
    private CompanyRepository companyRepository;
    private JwtUtilCandidate jwtUtil;
    private PasswordEncoder passwordEncoder;
    public static String username;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository, JobRepository jobRepository, CompanyRepository companyRepository,
                            JwtUtilCandidate jwtUtil, PasswordEncoder passwordEncoder) {
        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;

    }


    public String registerCandidate(CandidateRequestDto candidate) {
        // TODO Auto-generated method stub

        if(candidateRepository.findById(candidate.getEmail()).orElse(null) == null) {
            candidate.setPassword(new BCryptPasswordEncoder().encode(candidate.getPassword()));
            candidateRepository.save(convertToCandidateEntity(candidate));
            return "Registered Successfully";
        }

        return "Email already registered";
    }

    private Candidate convertToCandidateEntity(CandidateRequestDto candidateRequestDto) {
        Candidate candidate = new Candidate();

        candidate.setEmail(candidateRequestDto.getEmail());
        candidate.setPassword(candidateRequestDto.getPassword());
        candidate.setName(candidateRequestDto.getName());
        candidate.setExperience(candidateRequestDto.getExperience());
        candidate.setCompany(candidateRequestDto.getCompany());

        return candidate;
    }

    public List<JobPostDtoCandidate> getAllJobPost() {
        // TODO Auto-generated method stub
        List<JobPostDtoCandidate> list = new ArrayList<>();

        for(Company company : companyRepository.findAll()) {
            for(int jobPostId : company.getJobPostListAndDate().keySet()) {
                JobPostDtoCandidate jobPostDTOCandidate = getJobPostDTOCandidate(jobPostId);
                jobPostDTOCandidate.setJobPostDate(company.getJobPostListAndDate().get(jobPostId));
                list.add(jobPostDTOCandidate);
            }
        }

        return list;
    }

    public List<JobPostDtoCandidate> getJobsByQuery(String str) {
        // TODO Auto-generated method stub
        List<JobPost> jobPostList = jobRepository.searchJob(str);
        List<JobPostDtoCandidate> jobPostDtoCandidateList = new ArrayList<>();

        for(JobPost jobPost : jobPostList) {
            jobPostDtoCandidateList.add(getJobPostDTOCandidate(jobPost.getPostId()));
        }

        return jobPostDtoCandidateList;
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
        candidate.getJobPostListAndDate().put(jobPostId, LocalDate.now());
        candidateRepository.save(candidate);

        return "Successfully applied for " + jobPost.getPostProfile() + " role!";
    }

    public CandidateDto getCandidateProfile() {
        CandidateDto candidateDTO = new CandidateDto();

        Candidate candidate = candidateRepository.findByEmail(username);
        candidateDTO.setName(candidate.getName());
        candidateDTO.setCompany(candidate.getCompany());
        candidateDTO.setEmail(candidate.getEmail());
        candidateDTO.setPassword(candidate.getPassword());
        candidateDTO.setExperience(candidate.getExperience());
        List<JobPostDtoCandidate> list = new ArrayList<>();

        for(int jobid : candidate.getJobPostListAndDate().keySet()) {
            JobPostDtoCandidate jobPostDtoCandidate = getJobPostDTOCandidate(jobid);
            jobPostDtoCandidate.setJobPostDate(candidate.getJobPostListAndDate().get(jobid));
            list.add(jobPostDtoCandidate);
        }

        candidateDTO.setJobPostList(list);

        return candidateDTO;
    }

    private JobPostDtoCandidate getJobPostDTOCandidate(int jobid) {
        JobPost job = jobRepository.findById(jobid).orElse(new JobPost());
        JobPostDtoCandidate jobPostDTOCandidate = new JobPostDtoCandidate();

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

    public JobPostDtoCandidate withdrawJobApplication(Integer jobPostId) {

        Candidate candidate = candidateRepository.findByEmail(username);

        if(!candidate.getJobPostListAndDate().keySet().contains(jobPostId)) {
            return null;
        }

        JobPostDtoCandidate jobPostDtoCandidate = getJobPostDTOCandidate(jobPostId);
        jobPostDtoCandidate.setJobPostDate(candidate.getJobPostListAndDate().get(jobPostId));
        candidate.getJobPostListAndDate().remove(jobPostId);
        candidateRepository.save(candidate);

        JobPost jobPost = jobRepository.findById(jobPostId).orElse(new JobPost());
        jobPost.getCandidateList().remove(username);
        jobRepository.save(jobPost);

        return jobPostDtoCandidate;
    }

    public String deleteCandidate() {
        Candidate candidate = candidateRepository.findByEmail(username);

        for(int jobPostId : candidate.getJobPostListAndDate().keySet()) {
            JobPost jobPost = jobRepository.findById(jobPostId).orElse(new JobPost());
            jobPost.getCandidateList().remove(username);
            jobRepository.save(jobPost);
        }

        candidateRepository.deleteById(username);

        return username + " deleted successfully";
    }

    public List<JobPostDtoCandidate> getJobPostBySalary(long min, long max) {
        List<JobPost> allJobPost = jobRepository.findAll();
        List<JobPostDtoCandidate> filteredList = new ArrayList<>();

        for(JobPost jobPost : allJobPost) {
            if(jobPost.getSalary() < min || jobPost.getSalary() > max) {
                continue;
            }
            JobPostDtoCandidate jobPostDtoCandidate = getJobPostDTOCandidate(jobPost.getPostId());
            Company company = companyRepository.findByUsername(jobPost.getCompanyUsername());
            jobPostDtoCandidate.setJobPostDate(company.getJobPostListAndDate().get(jobPost.getPostId()));
            filteredList.add(jobPostDtoCandidate);
        }

        Collections.sort(filteredList, (o1, o2) -> (int)(o1.getSalary() - o2.getSalary()));
        return filteredList;
    }

    public List<JobPostDtoCandidate> getJobsByTechStack(List<String> techstack) {

        techstack = techstack
                .stream()
                .map(String::toLowerCase)
                .toList();

        List<JobPost> jobPostList = jobRepository.findByMatchingTechStack(techstack);
        List<JobPostDtoCandidate> jobPostDtoCandidateList = new ArrayList<>();


        for(JobPost jobPost : jobPostList) {
            boolean containsAll = true;
            List<String> postTechStack = jobPost.getPostTechStack()
                    .stream()
                    .map(String::toLowerCase)
                    .toList();


            for(String tech : techstack) {
                if(!postTechStack.contains(tech)) {
                    containsAll = false;
                    break;
                }
            }

            if (containsAll) {
                JobPostDtoCandidate jobPostDtoCandidate = getJobPostDTOCandidate(jobPost.getPostId());
                Company company = companyRepository.findByUsername(jobPost.getCompanyUsername());
                jobPostDtoCandidate.setJobPostDate(company.getJobPostListAndDate().get(jobPost.getPostId()));
                jobPostDtoCandidateList.add(jobPostDtoCandidate);
            }
        }

        return jobPostDtoCandidateList;
    }

    public String candidateLoginService(LoginInfo loginInfo) {
        String username = loginInfo.getUsername();
        String password = loginInfo.getPassword();

        Candidate userOpt = candidateRepository.findByEmail(username);

        if (userOpt == null || !passwordEncoder.matches(password, userOpt.getPassword())) {
            throw new InvalidCredentialsException("Invalid Candidate Credentials");
        }

        return jwtUtil.generateToken(username);
    }
}

