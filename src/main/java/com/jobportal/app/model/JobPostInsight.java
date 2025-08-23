package com.jobportal.app.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class JobPostInsight implements Comparable<JobPostInsight> {
    private int postId;
    private LocalDate jobPostDate;
    private int candidatesApplied;
    private Set<String> candidateList;

    @Override
    public int compareTo(JobPostInsight otherJob) {
        return otherJob.getCandidatesApplied() - this.candidatesApplied;
    }
}
