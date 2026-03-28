package com.jobly.service;

import com.jobly.dao.AdminUserActionDao;
import com.jobly.dao.UserDao;
import com.jobly.enums.Role;
import com.jobly.exception.general.BadRequestException;
import com.jobly.exception.general.NotFoundException;
import com.jobly.gen.model.AdminUserActionType;
import com.jobly.gen.model.AdminUserStatusManageRequest;
import com.jobly.model.AdminUserActionEntity;
import com.jobly.model.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static com.jobly.util.TestEntityFactory.buildUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTests {

    @Mock
    private UserDao userDao;

    @Mock
    private AdminUserActionDao adminUserActionDao;

    @InjectMocks
    private AdminUserService adminUserService;

    @Test
    void getSystemUsers_returnsListAndTotal() {
        UserEntity user = buildUser(1L, "John", "Doe", "john@jobly.test", "john");
        user.setRole(Role.USER);

        when(userDao.findSystemUsersBySearch("john", 0, 10)).thenReturn(List.of(user));
        when(userDao.countSystemUsersBySearch("john")).thenReturn(1);

        var response = adminUserService.getSystemUsers("john", 0, 10);

        assertEquals(1, response.getTotal());
        assertEquals(1, response.getUsers().size());
        assertEquals("john", response.getUsers().get(0).getUsername());
    }

    @Test
    void getAdminUserDetails_returnsDetailsWithActions() {
        UserEntity target = buildUser(2L, "Ana", "Vale", "ana@jobly.test", "ana");
        target.setRole(Role.EMPLOYER);

        UserEntity admin = buildUser(9L, "Alice", "Admin", "admin@jobly.test", "alice");
        admin.setRole(Role.ADMINISTRATOR);

        AdminUserActionEntity actionEntity = AdminUserActionEntity.builder()
                .id(99L)
                .action(AdminUserActionType.SUSPEND)
                .comment("Policy violation")
                .createdAt(OffsetDateTime.parse("2026-03-21T10:00:00Z"))
                .targetUser(target)
                .performedBy(admin)
                .build();

        when(userDao.findById(target.getId())).thenReturn(target);
        when(adminUserActionDao.findByTargetUserId(target.getId())).thenReturn(List.of(actionEntity));

        var response = adminUserService.getAdminUserDetails(target.getId());

        assertEquals(target.getId(), response.getUser().getId());
        assertEquals(1, response.getActions().size());
        assertEquals("alice", response.getActions().get(0).getPerformedBy().getUsername());
    }

    @Test
    void manageAdminUserStatus_suspendsUserAndLogsAction() {
        UserEntity target = buildUser(3L, "Eli", "Moss", "eli@jobly.test", "eli");
        target.setRole(Role.USER);
        target.setSuspended(false);

        UserEntity admin = buildUser(10L, "Ada", "Admin", "ada@jobly.test", "ada");
        admin.setRole(Role.ADMINISTRATOR);

        AdminUserStatusManageRequest request = new AdminUserStatusManageRequest()
                .action(AdminUserActionType.SUSPEND)
                .comment("Investigating reports");

        when(userDao.findById(target.getId())).thenReturn(target);
        when(userDao.findById(admin.getId())).thenReturn(admin);

        adminUserService.manageAdminUserStatus(target.getId(), admin.getId(), request);

        assertEquals(true, target.getSuspended());
        verify(userDao).save(target);
        verify(adminUserActionDao).save(any(AdminUserActionEntity.class));
    }

    @Test
    void manageAdminUserStatus_rejectsBlankComment() {
        AdminUserStatusManageRequest request = new AdminUserStatusManageRequest()
                .action(AdminUserActionType.SUSPEND)
                .comment(" ");

        assertThrows(BadRequestException.class, () -> adminUserService.manageAdminUserStatus(1L, 2L, request));
    }

    @Test
    void manageAdminUserStatus_rejectsAdminTarget() {
        UserEntity adminTarget = buildUser(11L, "Root", "Admin", "root@jobly.test", "root");
        adminTarget.setRole(Role.ADMINISTRATOR);

        AdminUserStatusManageRequest request = new AdminUserStatusManageRequest()
                .action(AdminUserActionType.RESTORE)
                .comment("Attempt");

        when(userDao.findById(adminTarget.getId())).thenReturn(adminTarget);

        assertThrows(NotFoundException.class, () -> adminUserService.manageAdminUserStatus(adminTarget.getId(), 2L, request));
    }

    @Test
    void manageAdminUserStatus_restoreRequiresSuspendedUser() {
        UserEntity target = buildUser(12L, "Nia", "Moon", "nia@jobly.test", "nia");
        target.setRole(Role.USER);
        target.setSuspended(false);

        AdminUserStatusManageRequest request = new AdminUserStatusManageRequest()
                .action(AdminUserActionType.RESTORE)
                .comment("Reinstated");

        when(userDao.findById(target.getId())).thenReturn(target);

        assertThrows(BadRequestException.class, () -> adminUserService.manageAdminUserStatus(target.getId(), 2L, request));
    }
}

