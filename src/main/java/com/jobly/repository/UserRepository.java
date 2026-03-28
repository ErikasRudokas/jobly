package com.jobly.repository;

import com.jobly.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByDisplayName(String username);

    @Query(value = """
            SELECT *
            FROM USERS
            WHERE ROLE IN ('USER', 'EMPLOYER')
              AND (
                LOWER(DISPLAY_NAME) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(EMAIL) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(CONCAT(FIRST_NAME, ' ', LAST_NAME)) LIKE LOWER(CONCAT('%', :search, '%'))
              )
            ORDER BY ID
            LIMIT :limit OFFSET :offset
            """,
            nativeQuery = true)
    List<UserEntity> findSystemUsersBySearch(String search, int limit, int offset);

    @Query(value = """
            SELECT COUNT(*)
            FROM USERS
            WHERE ROLE IN ('USER', 'EMPLOYER')
              AND (
                LOWER(DISPLAY_NAME) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(EMAIL) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(CONCAT(FIRST_NAME, ' ', LAST_NAME)) LIKE LOWER(CONCAT('%', :search, '%'))
              )
            """,
            nativeQuery = true)
    Integer countSystemUsersBySearch(String search);
}
