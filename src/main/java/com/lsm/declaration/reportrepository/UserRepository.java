package com.lsm.declaration.reportrepository;

import com.lsm.declaration.entities.UserEntity;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    UserEntity findByNickname(String nickname);

    UserEntity findByContact(String contact);

//    @Modifying
    @Transactional
//    @Query("select u. email FROM UserEntity u where u.email = u.email ")
//         반환 타입 수정: Optional<UserEntity>
    Optional<UserEntity> findByEmail(String username);
    @Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE u.isSuspended = true")
    boolean existsByIsSuspended();


}

