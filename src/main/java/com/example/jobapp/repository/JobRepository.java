package com.example.jobapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.jobapp.entity.JobPost;

@Repository
public interface JobRepository extends JpaRepository<JobPost, Integer> {
    @Query("""
        SELECT job FROM JobPost job WHERE
        LOWER(job.postProfile) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(job.company) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(job.postDesc) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(job.location) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    List<JobPost> searchJob(@Param("keyword") String keyword);


    @Query("""
        SELECT j FROM JobPost j
        JOIN j.postTechStack tech
        WHERE LOWER(tech) IN :inputTechs
        """)
    List<JobPost> findByMatchingTechStack(@Param("inputTechs") List<String> inputTechs);

}
