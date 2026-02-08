package com.jobly.repository;

import com.jobly.model.SkillAliasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillAliasRepository extends JpaRepository<SkillAliasEntity, Long> {
}
