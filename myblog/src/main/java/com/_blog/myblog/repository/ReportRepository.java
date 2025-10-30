package com._blog.myblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com._blog.myblog.model.ReportStruct;
import java.util.List;

public interface ReportRepository extends JpaRepository<ReportStruct, Integer> {

    List<ReportStruct> findByTargetUserId(Integer userId);
    List<ReportStruct> findByTargetPostId(Integer postId);
    List<ReportStruct> findByResolvedFalse();
}
