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
    List<BoardCommentEntity> findByIndex(@Param("index") int index);

}
