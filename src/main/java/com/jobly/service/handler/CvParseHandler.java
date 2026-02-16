package com.jobly.service.handler;

import com.jobly.dao.*;
import com.jobly.gen.api.parser.data.CvParseResponse;
import com.jobly.gen.api.parser.data.Education;
import com.jobly.gen.api.parser.data.WorkExperience;
import com.jobly.mapper.UserEducationMapper;
import com.jobly.mapper.UserSkillMapper;
import com.jobly.mapper.UserWorkExperienceMapper;
import com.jobly.model.SkillAliasEntity;
import com.jobly.model.SkillEntity;
import com.jobly.model.UserCvEntity;
import com.jobly.model.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CvParseHandler {

    private final CvDao cvDao;
    private final UserWorkExperienceDao userWorkExperienceDao;
    private final UserEducationDao userEducationDao;
    private final UserSkillDao userSkillDao;
    private final SkillDao skillDao;

    public void saveParsedCvData(UserCvEntity userCvEntity, CvParseResponse response) {
        UserEntity userEntity = userCvEntity.getUser();
        if (cvDao.existsByUserId(userEntity.getId())) {
            log.info("CV already exists in DB so no parsing is done for user with ID: {}", userEntity.getId());
            return;
        }

        log.info("Saving parsed CV data for user with ID: {}", userEntity.getId());
        var parsedWorkExperience = response.getWorkExperience();
        var parsedEducation = response.getEducation();
        var parsedSkills = response.getSkills();

        persistWorkExperience(userEntity, parsedWorkExperience);
        persistEducation(userEntity, parsedEducation);
        persistSkills(userEntity, parsedSkills);
    }

    private void persistWorkExperience(UserEntity userEntity, List<WorkExperience> parsedWorkExperience) {
        if (userWorkExperienceDao.existsByUserId(userEntity.getId())) {
            log.info("Work experience already exists in DB so no parsing is done for user with ID: {}", userEntity.getId());
            return;
        }

        var workExperienceToSave = parsedWorkExperience.stream()
                .map(workExperience -> UserWorkExperienceMapper.fromParsedDataToEntity(workExperience, userEntity))
                .toList();
        userWorkExperienceDao.saveAll(workExperienceToSave);
    }

    private void persistEducation(UserEntity userEntity, List<Education> parsedEducation) {
        if(userEducationDao.existsByUserId(userEntity.getId())) {
            log.info("Education already exists in DB so no parsing is done for user with ID: {}", userEntity.getId());
            return;
        }

        var educationToSave = parsedEducation.stream()
                .map(education -> UserEducationMapper.fromParsedDataToEntity(education, userEntity))
                .toList();
        userEducationDao.saveAll(educationToSave);
    }

    private void persistSkills(UserEntity userEntity, List<String> parsedSkills) {
        if (userSkillDao.existsByUserId(userEntity.getId())) {
            log.info("Skills already exists in DB so no parsing is done for user with ID: {}", userEntity.getId());
            return;
        }

        var skillAliases = skillDao.findAllSkillAliases();
        var skillsToSave = getMatchingSkillsFromParsedCv(parsedSkills, skillAliases);
        var userSkillsToSave = skillsToSave.stream()
                .map(skill -> UserSkillMapper.fromParsedDataToEntity(skill, userEntity))
                .toList();
        userSkillDao.saveAll(userSkillsToSave);
    }

    private Set<SkillEntity> getMatchingSkillsFromParsedCv(List<String> parsedSkills, Map<String, SkillAliasEntity> skillAliases) {
        return parsedSkills.stream()
                .map(this::cleanText)
                .flatMap(cleaned ->
                        skillAliases.keySet().stream()
                                .filter(cleaned::contains)
                                .map(alias -> skillAliases.get(alias).getSkill())
                )
                .collect(Collectors.toSet());
    }

    private String cleanText(String input) {
        return input.toLowerCase()
                .replaceAll("https?://\\S+", "")
                .replaceAll("www\\.\\S+", "")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
