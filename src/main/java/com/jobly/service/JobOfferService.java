package com.jobly.service;

import com.jobly.dao.ApplicationDao;
import com.jobly.dao.JobOfferDao;
import com.jobly.dao.JobSkillDao;
import com.jobly.dao.SkillDao;
import com.jobly.exception.general.BadRequestException;
import com.jobly.exception.general.ForbiddenException;
import com.jobly.gen.model.*;
import com.jobly.mapper.ApplicationMapper;
import com.jobly.mapper.JobOfferMapper;
import com.jobly.mapper.JobSkillMapper;
import com.jobly.model.JobOfferEntity;
import com.jobly.model.JobSkillEntity;
import com.jobly.model.SkillEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobOfferService {

    private final JobOfferDao jobOfferDao;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CvService cvService;
    private final ApplicationDao applicationDao;
    private final JobSkillDao jobSkillDao;
    private final SkillDao skillDao;

    public GetAllJobOffersResponse findAll() {
        var jobOffers = jobOfferDao.findAll().stream()
                .map(JobOfferMapper::toJobOfferListObject)
                .toList();

        return new GetAllJobOffersResponse()
                .jobOffers(jobOffers)
                .total(jobOffers.size());
    }

    public JobOfferDetailsResponse findById(Long id) {
        var jobOffer = jobOfferDao.findById(id);
        var jobSkills = jobSkillDao.findAllByJobOfferId(id);
        return JobOfferMapper.toJobOfferDetailsResponse(jobOffer, jobSkills);
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    public GetMineJobOffersResponse findJobOffersByUserId(Long userId) {
        var jobOffers = jobOfferDao.findByUserId(userId).stream()
                .map(JobOfferMapper::toJobOfferListObject)
                .toList();

        return new GetMineJobOffersResponse()
                .jobOffers(jobOffers)
                .total(jobOffers.size());
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    public JobOfferWithApplicationsResponse findOwnedJobOfferDetails(Long id, Long userId) {
        var jobOffer = jobOfferDao.findById(id);
        if (!jobOffer.getCreator().getId().equals(userId)) {
            throw new ForbiddenException("User is not the owner of the job offer");
        }
        var applications = applicationDao.findAllPendingByJobOfferId(id);
        var jobSkills = jobSkillDao.findAllByJobOfferId(id);
        return JobOfferMapper.toJobOfferWithApplicationsResponse(jobOffer, applications, jobSkills);
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @Transactional
    public JobOffer createJobOffer(CreateJobOfferRequest createJobOfferRequest, Long userId) {
        var user = userService.findById(userId);
        var category = categoryService.findEntityById(createJobOfferRequest.getCategoryId());
        JobOfferEntity jobOfferToSave = JobOfferMapper.toJobOfferEntity(createJobOfferRequest, user, category);
        JobOfferEntity savedJobOffer = jobOfferDao.save(jobOfferToSave);
        List<JobSkillEntity> savedSkills = persistJobSkillsForCreateFlow(createJobOfferRequest, savedJobOffer);
        return JobOfferMapper.toJobOffer(savedJobOffer, savedSkills);
    }

    private List<JobSkillEntity> persistJobSkillsForCreateFlow(CreateJobOfferRequest createJobOfferRequest, JobOfferEntity jobOfferEntity) {
        Set<SkillEntity> skillEntities = skillDao.findAllByIds(getSkillIdsFromRequest(createJobOfferRequest));
        List<JobSkillEntity> jobSkillEntities = Optional.ofNullable(createJobOfferRequest.getSkills())
                .orElseGet(List::of)
                .stream()
                .map(skill -> {
                    SkillEntity matchingSkillEntity = getMatchingSkillEntity(skill.getSkillId(), skillEntities);
                    return JobSkillMapper.toJobSkillEntity(matchingSkillEntity, jobOfferEntity, skill.getProficiency());
                })
                .toList();
        return jobSkillDao.saveAll(jobSkillEntities);
    }

    private static List<Long> getSkillIdsFromRequest(CreateJobOfferRequest createJobOfferRequest) {
        return Optional.ofNullable(createJobOfferRequest.getSkills())
                .orElseGet(List::of)
                .stream()
                .map(CreateJobOfferSkill::getSkillId)
                .toList();
    }

    private static SkillEntity getMatchingSkillEntity(Long skillId, Set<SkillEntity> skillEntities) {
        return skillEntities.stream()
                .filter(skillEntity -> skillEntity.getId().equals(skillId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Skill with id " + skillId + " not found"));
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @Transactional
    public JobOffer updateJobOffer(UpdateJobOfferRequest updateJobOfferRequest, Long jobOfferId, Long userId) {
        var jobOffer = jobOfferDao.findById(jobOfferId);
        if (!jobOffer.getCreator().getId().equals(userId)) {
            throw new ForbiddenException("User is not the owner of the job offer");
        }
        var category = categoryService.findEntityById(updateJobOfferRequest.getCategoryId());
        JobOfferMapper.updateJobOfferEntity(jobOffer, updateJobOfferRequest, category);
        JobOfferEntity savedJobOffer = jobOfferDao.save(jobOffer);

        Set<SkillEntity> skillEntities = skillDao.findAllByIds(getSkillIdsFromRequest(updateJobOfferRequest));
        List<Long> jobSkillsToDelete = getSkillIdsMarkedForDeletion(updateJobOfferRequest);
        log.info("Job skills marked for deletion: {}", jobSkillsToDelete);
        List<UpdateJobOfferSkill> jobSkillsToSave = getSkillsNotMarkedForDeletion(updateJobOfferRequest);
        List<JobSkillEntity> existingJobSkills = jobSkillDao.findAllByJobOfferId(jobOfferId);

        jobSkillsToSave.forEach(skill -> {
            var matchingJobSkill = getMatchingExistingJobSkill(skill, existingJobSkills);
            if (matchingJobSkill.isPresent()) {
                matchingJobSkill.get().setExpectedProficiency(skill.getProficiency());
            } else {
                var matchingSkillEntity = getMatchingSkillEntity(skill.getSkillId(), skillEntities);
                var newJobSkill = JobSkillMapper.toJobSkillEntity(matchingSkillEntity, savedJobOffer, skill.getProficiency());
                existingJobSkills.add(newJobSkill);
            }
        });
        jobSkillDao.saveAll(existingJobSkills);
        jobSkillDao.deleteAllByIds(jobSkillsToDelete, jobOfferId);
        var updatedJobSkills = jobSkillDao.findAllByJobOfferId(jobOfferId);
        return JobOfferMapper.toJobOffer(savedJobOffer, updatedJobSkills);
    }

    private static List<Long> getSkillIdsFromRequest(UpdateJobOfferRequest updateJobOfferRequest) {
        return Optional.ofNullable(updateJobOfferRequest.getSkills())
                .orElseGet(List::of)
                .stream()
                .map(UpdateJobOfferSkill::getSkillId)
                .toList();
    }

    private static List<Long> getSkillIdsMarkedForDeletion(UpdateJobOfferRequest updateJobOfferRequest) {
        return Optional.ofNullable(updateJobOfferRequest.getSkills())
                .orElseGet(List::of)
                .stream()
                .filter(skill -> Boolean.TRUE.equals(skill.getDelete()))
                .map(UpdateJobOfferSkill::getSkillId)
                .toList();
    }

    private static List<UpdateJobOfferSkill> getSkillsNotMarkedForDeletion(UpdateJobOfferRequest updateJobOfferRequest) {
        return Optional.ofNullable(updateJobOfferRequest.getSkills())
                .orElseGet(List::of)
                .stream()
                .filter(skill -> !Boolean.TRUE.equals(skill.getDelete()))
                .toList();
    }

    private static Optional<JobSkillEntity> getMatchingExistingJobSkill(UpdateJobOfferSkill skill, List<JobSkillEntity> existingJobSkills) {
        return existingJobSkills.stream()
                .filter(jobSkill -> jobSkill.getSkill().getId().equals(skill.getSkillId()))
                .findFirst();
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    public void deleteJobOffer(Long id, Long userId) {
        var jobOffer = jobOfferDao.findById(id);
        if (!jobOffer.getCreator().getId().equals(userId)) {
            throw new ForbiddenException("User is not the owner of the job offer");
        }
        jobOfferDao.delete(jobOffer);
    }

    @PreAuthorize("hasRole('USER')")
    public Application applyToJobOffer(Long jobOfferId, Long userId, ApplicationCreateRequest applicationCreateRequest) {
        var applicant = userService.findById(userId);
        var jobOffer = jobOfferDao.findById(jobOfferId);
        var userCv = cvService.findActiveCvByUserId(userId);

        if (applicationDao.isUserAlreadyAppliedToJobOffer(userId, jobOfferId)) {
            throw new BadRequestException("User has already applied to this job offer");
        }

        var application = ApplicationMapper.toApplicationEntity(applicationCreateRequest, applicant, jobOffer, userCv);
        return ApplicationMapper.toApplication(applicationDao.saveApplication(application));
    }

    @PreAuthorize("hasRole('USER')")
    public CanApplyResponse canApplyToJobOffer(Long jobOfferId, Long userId) {
        var canApply = !applicationDao.isUserAlreadyAppliedToJobOffer(userId, jobOfferId);
        return new CanApplyResponse().canApply(canApply);
    }
}
