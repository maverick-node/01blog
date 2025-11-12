package com.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Model.ReportStruct;

public interface ReportRepo extends JpaRepository<ReportStruct, Integer>{
    
}
