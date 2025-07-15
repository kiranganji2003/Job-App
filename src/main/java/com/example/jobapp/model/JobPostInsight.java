package com.example.jobapp.model;

import lombok.Data;

import java.util.Set;

@Data
public class JobPostInsight implements Comparable<JobPostInsight> {
    private int postId;
    private int candidatesApplied;
    private Set<String> candidateList;

    @Override
    public int compareTo(JobPostInsight otherJob) {
        return otherJob.getCandidatesApplied() - this.candidatesApplied;
    }
}
