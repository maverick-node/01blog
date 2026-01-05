package com.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Model.ReportStruct;

public interface ReportRepo extends JpaRepository<ReportStruct, Integer> {
        ReportStruct findById(int id);

        ReportStruct findByReportedPostId(Integer reportedPostId);
        List<ReportStruct>      findAllByReportedPostId(Integer reportedPostId);
        List<ReportStruct> findAll();


}
