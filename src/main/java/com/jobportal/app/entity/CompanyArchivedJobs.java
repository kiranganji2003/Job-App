package com.jobportal.app.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Data
public class CompanyArchivedJobs {
    @Id
    private String username;

    @ElementCollection
    private Map<Integer, LocalDate> archivedJobPostList = new HashMap<>();
}
