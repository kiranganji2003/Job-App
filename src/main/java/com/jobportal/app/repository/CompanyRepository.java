package com.jobportal.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jobportal.app.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {

    Company findByUsername(String username);

}
