package com.jobly.service;

import com.jobly.dao.ApplicationDao;
import com.jobly.exception.general.BadRequestException;
import com.jobly.exception.general.ForbiddenException;
import com.jobly.gen.model.*;
import com.jobly.mapper.ApplicationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    private final ApplicationDao applicationDao;
    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    public GetMyApplicationsResponse getMyApplications(Long userId) {
        var applications = applicationDao.findAllByUserId(userId).stream()
                .map(ApplicationMapper::toApplicationListObject)
                .toList();

        return new GetMyApplicationsResponse()
                .applications(applications)
                .total(applications.size());
    }

    @PreAuthorize("hasRole('USER')")
    public MyApplication getMyApplication(Long applicationId, Long userId) {
        var application = applicationDao.findApplicationOfUser(userId, applicationId);
        return ApplicationMapper.toMyApplication(application);
    }

    @PreAuthorize("hasRole('USER')")
    public void cancelApplication(Long applicationId, Long userId) {
        var application = applicationDao.findApplicationOfUser(userId, applicationId);
        if (!ApplicationStatus.PENDING.equals(application.getStatus())) {
            throw new BadRequestException("Only pending applications can be cancelled");
        }
        application.setStatus(ApplicationStatus.WITHDRAWN);
        applicationDao.save(application);
    }

    @PreAuthorize("hasRole('USER')")
    public Application updateApplication(Long applicatioId, Long userId, ApplicationUpdateRequest request) {
        var application = applicationDao.findApplicationOfUser(userId, applicatioId);
        if (!ApplicationStatus.PENDING.equals(application.getStatus())) {
            throw new BadRequestException("Only pending applications can be updated");
        }

        application.setComment(request.getComment());
        applicationDao.save(application);
        return ApplicationMapper.toApplication(application);
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    public void manageApplication(Long applicationId, Long userId, ApplicationManageRequest request) {
        var application = applicationDao.findById(applicationId);
        if (!application.getJobOffer().getCreator().getId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to manage this application");
        }

        if (!ApplicationStatus.PENDING.equals(application.getStatus())) {
            throw new BadRequestException("Only pending applications can be managed");
        }

        switch (request.getAction()) {
            case ApplicationManageAction.REJECT -> application.setStatus(ApplicationStatus.REJECTED);
            case ApplicationManageAction.APPROVE -> application.setStatus(ApplicationStatus.ACCEPTED);
            default -> throw new BadRequestException("Invalid action");
        }
        applicationDao.save(application);
    }
}
