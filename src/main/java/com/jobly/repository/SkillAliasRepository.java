package com.jobly.repository;

import com.jobly.model.SkillAliasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillAliasRepository extends JpaRepository<SkillAliasEntity, Long> {

	List<SkillAliasEntity> findAllBySkillId(Long skillId);
}
