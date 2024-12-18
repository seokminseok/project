package com.lsm.declaration.reportrepository;



import com.lsm.declaration.entities.BoardCommentEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardCommentEntity, Long> {
    @Query("SELECT b.index FROM BoardCommentEntity b WHERE b.userEmail = :reportedEmail")
    List<Integer> findIndexesByReportedEmail(@Param("reportedEmail") String reportedEmail);

}
