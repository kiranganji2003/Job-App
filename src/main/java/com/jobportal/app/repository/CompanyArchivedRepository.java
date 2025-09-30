package com.jobportal.app.repository;

import com.jobportal.app.entity.CompanyArchivedJobs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyArchivedRepository extends JpaRepository<CompanyArchivedJobs, String> {
}
