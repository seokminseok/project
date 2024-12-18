package com.lsm.declaration.reportrepository;

import com.lsm.declaration.entities.ReportEntity;
import com.lsm.declaration.entities.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    @Query("SELECT r FROM fave r WHERE r.userEmail = :userEmail AND r.reportedUserEmail = :reportedUserEmail")
    Optional<ReportEntity> findByUserEmailAndReportedUserEmail(
            @Param("userEmail") String userEmail,
            @Param("reportedUserEmail") String reportedUserEmail
    );
    List<ReportEntity> findByCurrentStatus(String currentStatus);
}


