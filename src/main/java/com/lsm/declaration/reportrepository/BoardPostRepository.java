package com.lsm.declaration.reportrepository;

import com.lsm.declaration.entities.BoardPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardPostRepository extends JpaRepository<BoardPostEntity, Long> {
    @Query("SELECT b.index FROM BoardPostEntity b WHERE b.userEmail = :userEmail")
    int findIndexesByReportedEmail(String userEmail);
}
