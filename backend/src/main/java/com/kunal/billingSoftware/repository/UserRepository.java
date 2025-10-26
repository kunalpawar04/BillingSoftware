package com.kunal.billingSoftware.repository;

import com.kunal.billingSoftware.entity.UserEntity;
import com.kunal.billingSoftware.projection.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUserId(String userId);

    List<UserProjection> findAllProjectedBy();
}