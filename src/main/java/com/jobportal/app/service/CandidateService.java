package com.jobportal.app.service;

import com.jobportal.app.entity.Company;
import com.jobportal.app.entity.JobPost;
import com.jobportal.app.exception.AlreadyRegisteredException;
import com.jobportal.app.exception.InvalidCredentialsException;
import com.jobportal.app.exception.InvalidJobPostIdException;
import com.jobportal.app.model.*;
import com.jobportal.app.repository.CompanyRepository;
import com.jobportal.app.repository.JobRepository;
import com.jobportal.app.security.JwtUtilCandidate;
import com.jobportal.app.utility.AppMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobportal.app.entity.Candidate;
import com.jobportal.app.repository.CandidateRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CandidateService {

    private CandidateRepository candidateRepository;
    private JobRepository jobRepository;
    private CompanyRepository companyRepository;
    private JwtUtilCandidate jwtUtil;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository, JobRepository jobRepository, CompanyRepository companyRepository,
                            JwtUtilCandidate jwtUtil, PasswordEncoder passwordEncoder) {
        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;

    }

    private String getLoggedInUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Message registerCandidate(CandidateRequestDto candidate) {

        if(candidateRepository.findById(candidate.getEmail()).orElse(null) != null) {
            throw new AlreadyRegisteredException(String.format(AppMessages.EMAIL_ALREADY_REGISTERED, candidate.getEmail()));
        }

        candidate.setPassword(new BCryptPasswordEncoder().encode(candidate.getPassword()));
        candidateRepository.save(convertToCandidateEntity(candidate));
        return new Message(AppMessages.REGISTERED_SUCCESSFULLY);
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

        List<JobPost> allJobPost = jobRepository.findAll();

        List<JobPostDtoCandidate> list = new ArrayList<>();

        for(JobPost jobPost : allJobPost) {
            JobPostDtoCandidate jobPostDTOCandidate = getJobPostDTOCandidate(jobPost.getPostId());
            Company company = companyRepository.findByUsername(jobPost.getCompanyUsername());
            jobPostDTOCandidate.setJobPostDate(company.getJobPostListAndDate().get(jobPost.getPostId()));
            list.add(jobPostDTOCandidate);
        }

        return list;
    }

    public List<JobPostDtoCandidate> getJobsByQuery(String str) {
        List<JobPost> jobPostList = jobRepository.searchJob(str);
        List<JobPostDtoCandidate> jobPostDtoCandidateList = new ArrayList<>();

        for(JobPost jobPost : jobPostList) {
            Company company = companyRepository.findByUsername(jobPost.getCompanyUsername());
            JobPostDtoCandidate jobPostDtoCandidate = getJobPostDTOCandidate(jobPost.getPostId());
            jobPostDtoCandidate.setJobPostDate(company.getJobPostListAndDate().get(jobPost.getPostId()));
            jobPostDtoCandidateList.add(jobPostDtoCandidate);
        }

        return jobPostDtoCandidateList;
    }

    public Message applyJobPost(Integer jobPostId) {

        JobPost jobPost = jobRepository.findById(jobPostId).orElse(null);

        if(jobPost == null) {
            throw new InvalidJobPostIdException(String.format(AppMessages.NO_SUCH_JOB_POST, jobPostId));
        }

        jobPost.getCandidateList().add(getLoggedInUsername());
        jobRepository.save(jobPost);

        Candidate candidate = candidateRepository.findByEmail(getLoggedInUsername());
        candidate.getJobPostListAndDate().put(jobPostId, LocalDate.now());
        candidateRepository.save(candidate);

        return new Message(String.format(AppMessages.SUCCESSFULLY_APPLIED, jobPost.getPostProfile()));
    }

    public CandidateDto getCandidateProfile() {
        CandidateDto candidateDTO = new CandidateDto();

        Candidate candidate = candidateRepository.findByEmail(getLoggedInUsername());
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

        Candidate candidate = candidateRepository.findByEmail(getLoggedInUsername());

        if(!candidate.getJobPostListAndDate().keySet().contains(jobPostId)) {
            throw new InvalidJobPostIdException(String.format(AppMessages.NOT_APPLIED_JOBPOST, jobPostId));
        }

        JobPostDtoCandidate jobPostDtoCandidate = getJobPostDTOCandidate(jobPostId);
        jobPostDtoCandidate.setJobPostDate(candidate.getJobPostListAndDate().get(jobPostId));
        candidate.getJobPostListAndDate().remove(jobPostId);
        candidateRepository.save(candidate);

        JobPost jobPost = jobRepository.findById(jobPostId).orElse(new JobPost());
        jobPost.getCandidateList().remove(getLoggedInUsername());
        jobRepository.save(jobPost);

        return jobPostDtoCandidate;
    }

    public String deleteCandidate() {
        Candidate candidate = candidateRepository.findByEmail(getLoggedInUsername());

        for(int jobPostId : candidate.getJobPostListAndDate().keySet()) {
            JobPost jobPost = jobRepository.findById(jobPostId).orElse(new JobPost());
            jobPost.getCandidateList().remove(getLoggedInUsername());
            jobRepository.save(jobPost);
        }

        candidateRepository.deleteById(getLoggedInUsername());

        return String.format(AppMessages.USER_DELETED_SUCCESSFULLY, getLoggedInUsername());
    }

    public List<JobPostDtoCandidate> getJobPostBySalary(long min, long max) {
        List<JobPost> allJobPost = jobRepository.findBySalaryRange((double) min, (double) max);
        List<JobPostDtoCandidate> filteredList = new ArrayList<>();

        for(JobPost jobPost : allJobPost) {
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
            throw new InvalidCredentialsException(AppMessages.INVALID_CANDIDATE_CREDENTIALS);
        }

        return jwtUtil.generateToken(username);
    }

    public List<JobPostDtoCandidate> getJobsByLastNDays(Integer days) {

        LocalDate lastNthDay = LocalDate.now().minusDays(days);
        List<JobPost> jobPostList = jobRepository.findAll();
        List<JobPostDtoCandidate> result = new ArrayList<>();

        for(JobPost jobPost : jobPostList) {
            Company company = companyRepository.findByUsername(jobPost.getCompanyUsername());
            LocalDate jobPostDate = company.getJobPostListAndDate().get(jobPost.getPostId());

            if(jobPostDate.isAfter(lastNthDay)) {
                JobPostDtoCandidate jobPostDtoCandidate = getJobPostDTOCandidate(jobPost.getPostId());
                jobPostDtoCandidate.setJobPostDate(jobPostDate);
                result.add(jobPostDtoCandidate);
            }
        }

        return result;
    }

    public List<JobPostDtoCandidate> getJobsByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JobPost> jobPostPage = jobRepository.findAll(pageable);
        List<JobPost> allJobPost = jobPostPage.getContent();

        List<JobPostDtoCandidate> list = new ArrayList<>();

        for(JobPost jobPost : allJobPost) {
            JobPostDtoCandidate jobPostDTOCandidate = getJobPostDTOCandidate(jobPost.getPostId());
            Company company = companyRepository.findByUsername(jobPost.getCompanyUsername());
            jobPostDTOCandidate.setJobPostDate(company.getJobPostListAndDate().get(jobPost.getPostId()));
            list.add(jobPostDTOCandidate);
        }

        return list;
    }
}

