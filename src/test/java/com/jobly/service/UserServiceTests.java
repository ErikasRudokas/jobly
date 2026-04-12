package com.jobly.service;

import com.jobly.dao.CvDao;
import com.jobly.dao.UserDao;
import com.jobly.enums.CvStatus;
import com.jobly.enums.Role;
import com.jobly.exception.specific.NotUniqueUsernameException;
import com.jobly.gen.model.GetUserDetailsResponse;
import com.jobly.gen.model.ModifyUserDetailsRequest;
import com.jobly.gen.model.UserRegisterRequest;
import com.jobly.gen.model.UserRegisterResponse;
import com.jobly.model.UserCvEntity;
import com.jobly.model.UserEntity;
import com.jobly.util.TestEntityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.jobly.util.TestEntityFactory.buildUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CvDao cvDao;

    @InjectMocks
    private UserService userService;

    @Test
    void findByEmail_returnsOptionalFromDao() {
        UserEntity user = buildUser(1L, "John", "Doe", "john@jobly.test", "john");

        when(userDao.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Optional<UserEntity> response = userService.findByEmail(user.getEmail());

        assertTrue(response.isPresent());
        assertEquals(user, response.get());
    }

    @Test
    void existsByEmail_delegatesToDao() {
        when(userDao.existsByEmail("john@jobly.test")).thenReturn(true);

        boolean exists = userService.existsByEmail("john@jobly.test");

        assertTrue(exists);
    }

    @Test
    void existsByUsername_delegatesToDao() {
        when(userDao.existsByDisplayName("john")).thenReturn(true);

        boolean exists = userService.existsByUsername("john");

        assertTrue(exists);
    }

    @Test
    void save_encodesPasswordAndReturnsResponse() {
        UserRegisterRequest request = new UserRegisterRequest()
                .firstName("Jane")
                .lastName("Roe")
                .username("jane")
                .email("jane@jobly.test")
                .role(UserRegisterRequest.RoleEnum.USER)
                .password("secret");

        when(passwordEncoder.encode("secret")).thenReturn("encoded");

        UserEntity savedEntity = UserEntity.builder()
                .id(3L)
                .firstName("Jane")
                .lastName("Roe")
                .displayName("jane")
                .email("jane@jobly.test")
                .passwordHash("encoded")
                .role(Role.USER)
                .build();

        when(userDao.save(any(UserEntity.class))).thenReturn(savedEntity);

        UserRegisterResponse response = userService.save(request);

        assertEquals(savedEntity.getId(), response.getId());
        assertEquals(savedEntity.getEmail(), response.getEmail());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userDao).save(captor.capture());
        assertEquals("encoded", captor.getValue().getPasswordHash());
    }

    @Test
    void findById_returnsEntity() {
        UserEntity user = buildUser(4L, "Sam", "Ray", "sam@jobly.test", "sam");

        when(userDao.findById(user.getId())).thenReturn(user);

        UserEntity response = userService.findById(user.getId());

        assertEquals(user, response);
    }

    @Test
    void isCurrentUserEmployer_returnsTrueWhenRoleMatches() {
        UserEntity user = buildUser(5L, "Emma", "West", "emma@jobly.test", "emma");
        user.setRole(Role.EMPLOYER);

        when(userDao.findById(user.getId())).thenReturn(user);

        assertTrue(userService.isCurrentUserEmployer(user.getId()));
    }

    @Test
    void updateUserDetails_updatesFieldsAndReturnsCvId() {
        UserEntity user = buildUser(10L, "Sam", "Ray", "sam@jobly.test", "sam");
        UserCvEntity cvEntity = TestEntityFactory.buildUserCv(99L, user, "sam-cv.pdf", new byte[]{1});
        ModifyUserDetailsRequest request = new ModifyUserDetailsRequest()
                .username("samuel")
                .firstName("Samuel")
                .lastName("Raynor");

        when(userDao.findById(user.getId())).thenReturn(user);
        when(userDao.existsByDisplayName("samuel")).thenReturn(false);
        when(userDao.save(user)).thenReturn(user);
        when(cvDao.findOptionalByUserIdAndStatus(user.getId(), CvStatus.ACTIVE)).thenReturn(cvEntity);

        GetUserDetailsResponse response = userService.updateUserDetails(user.getId(), request);

        assertEquals("samuel", response.getUsername());
        assertEquals("Samuel", response.getFirstName());
        assertEquals("Raynor", response.getLastName());
        assertEquals(cvEntity.getId(), response.getCvId());
    }

    @Test
    void updateUserDetails_rejectsDuplicateUsername() {
        UserEntity user = buildUser(13L, "Max", "Lee", "max@jobly.test", "max");
        ModifyUserDetailsRequest request = new ModifyUserDetailsRequest()
                .username("taken");

        when(userDao.findById(user.getId())).thenReturn(user);
        when(userDao.existsByDisplayName("taken")).thenReturn(true);

        assertThrows(NotUniqueUsernameException.class, () -> userService.updateUserDetails(user.getId(), request));
    }
}
