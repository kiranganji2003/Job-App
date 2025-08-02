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

import java.time.LocalDate;
import java.util.*;

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

        return "Email already registered";
    }

    public List<JobPostDTOCandidate> getAllJobPost() {
        // TODO Auto-generated method stub
        List<JobPostDTOCandidate> list = new ArrayList<>();

        for(JobPost jobPost : jobRepository.findAll()) {
            list.add(getJobPostDTOCandidate(jobPost.getPostId()));
        }

        return list;
    }

    public List<JobPostDTOCandidate> getJobsByQuery(String str) {
        // TODO Auto-generated method stub
        List<JobPost> jobPostList = jobRepository.searchJob(str);
        List<JobPostDTOCandidate> jobPostDTOCandidateList = new ArrayList<>();

        for(JobPost jobPost : jobPostList) {
            jobPostDTOCandidateList.add(getJobPostDTOCandidate(jobPost.getPostId()));
        }

        return  jobPostDTOCandidateList;
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

    public CandidateDTO getCandidateProfile() {
        CandidateDTO candidateDTO = new CandidateDTO();

        Candidate candidate = candidateRepository.findByEmail(username);
        candidateDTO.setName(candidate.getName());
        candidateDTO.setCompany(candidate.getCompany());
        candidateDTO.setEmail(candidate.getEmail());
        candidateDTO.setPassword(candidate.getPassword());
        candidateDTO.setExperience(candidate.getExperience());
        List<JobPostDTOCandidate> list = new ArrayList<>();

        for(int jobid : candidate.getJobPostListAndDate().keySet()) {
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

        Candidate candidate = candidateRepository.findByEmail(username);

        if(!candidate.getJobPostListAndDate().keySet().contains(jobPostId)) {
            return null;
        }

        candidate.getJobPostListAndDate().remove(jobPostId);
        candidateRepository.save(candidate);

        JobPost jobPost = jobRepository.findById(jobPostId).orElse(new JobPost());
        jobPost.getCandidateList().remove(username);
        jobRepository.save(jobPost);

        return getJobPostDTOCandidate(jobPostId);
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

    public List<JobPostDTOCandidate> getJobPostBySalary(long min, long max) {
        List<JobPost> allJobPost = jobRepository.findAll();
        List<JobPostDTOCandidate> filteredList = new ArrayList<>();

        for(JobPost jobPost : allJobPost) {
            if(jobPost.getSalary() < min || jobPost.getSalary() > max) {
                continue;
            }
            filteredList.add(getJobPostDTOCandidate(jobPost.getPostId()));
        }

        Collections.sort(filteredList, (o1, o2) -> (int)(o1.getSalary() - o2.getSalary()));
        return filteredList;
    }

    public List<JobPostDTOCandidate> getJobsByTechStack(List<String> techstack) {

        techstack = techstack
                .stream()
                .map(String::toLowerCase)
                .toList();

        List<JobPost> jobPostList = jobRepository.findByMatchingTechStack(techstack);
        List<JobPostDTOCandidate> jobPostDTOCandidateList = new ArrayList<>();


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
                jobPostDTOCandidateList.add(getJobPostDTOCandidate(jobPost.getPostId()));
            }
        }

        return jobPostDTOCandidateList;
    }
}

