package com.jobly.repository;

import com.jobly.model.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<SkillEntity, Long> {

    @Query(value = """
            SELECT DISTINCT s.*
            FROM SKILLS s
            JOIN SKILL_ALIASES sa ON sa.skill_id = s.id
            WHERE LOWER(sa.alias) LIKE LOWER(CONCAT('%', :value, '%'))
            LIMIT :limit OFFSET :offset
        """,
    nativeQuery = true)
    List<SkillEntity> findAllSkillsByAliasSearch(String value, int limit, int offset);

    @Query(value = """
            SELECT COUNT(DISTINCT s.id)
            FROM SKILLS s
            JOIN SKILL_ALIASES sa ON sa.skill_id = s.id
            WHERE LOWER(sa.alias) LIKE LOWER(CONCAT('%', :value, '%'))
        """,
    nativeQuery = true)
    Integer countAllSkillsByAliasSearch(String value);
}
