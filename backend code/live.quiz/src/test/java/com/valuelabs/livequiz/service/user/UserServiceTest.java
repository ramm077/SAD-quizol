package com.valuelabs.livequiz.service.user;

import com.valuelabs.livequiz.model.dto.request.LoginDTO;
import com.valuelabs.livequiz.model.dto.request.UpdateUserDTO;
import com.valuelabs.livequiz.model.dto.request.UserCreationDTO;
import com.valuelabs.livequiz.model.entity.User;
import com.valuelabs.livequiz.model.enums.Role;
import com.valuelabs.livequiz.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DisplayUserService displayUserService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private UserService userService;

    @Test
    void testChangePassword() {
        LoginDTO loginDTO = new LoginDTO("john@example.com", "@12newPassword");

        User user = new User(1L, "John", "Doe", "john@example.com", "@12oldPassword", "+123456789", Role.RESPONDER, false,
                new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");

        Mockito.when(displayUserService.getUserByEmailIdAndInActive(Mockito.anyString(), Mockito.eq(false))).thenReturn(user);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(loginDTO.password());
        userService.changePassword(loginDTO);

        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(Mockito.anyString());
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));

        Assertions.assertTrue(user.getPassword().startsWith("@12"));
    }

    @Test
    void testUpdateUserById() {
        Long userId = 1L;
        UpdateUserDTO updateUserDTO = new UpdateUserDTO("John", "Doe", "+987654321");

        User user = new User(userId, "John", "Doe", "john@example.com", "oldPassword", "+123456789", Role.RESPONDER, false,
                new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");

        Mockito.when(userRepository.findByUserIdAndInActive(Mockito.anyLong(), Mockito.eq(false))).thenReturn(Optional.of(user));
        Mockito.when(authenticationService.getCurrentUserName()).thenReturn("admin");

        Boolean result = userService.updateUserById(userId, updateUserDTO);

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));

        Assertions.assertTrue(result);
        Assertions.assertEquals("John", user.getFirstName());
        Assertions.assertEquals("Doe", user.getLastName());
        Assertions.assertEquals("+987654321", user.getPhoneNumber());
        Assertions.assertEquals("admin", user.getUpdatedBy());
    }

    @Test
    void testDeleteActivePersonById() {
        Long userId = 1L;

        User user = new User(userId, "John", "Doe", "john@example.com", "oldPassword", "+123456789", Role.RESPONDER, false,
                new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");

        Mockito.when(userRepository.findByUserIdAndInActive(Mockito.anyLong(), Mockito.eq(false))).thenReturn(Optional.of(user));
        Mockito.when(authenticationService.getCurrentUserName()).thenReturn("admin");

        Boolean result = userService.deleteActivePersonById(userId);

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));

        Assertions.assertTrue(result);
        Assertions.assertTrue(user.getInActive());
        Assertions.assertEquals("admin", user.getUpdatedBy());
    }

    @Test
    void testIfExistReactivateUser() {
        UserCreationDTO userCreationDTO = new UserCreationDTO("John", "Doe", "john@example.com", "@12newPassword", "+987654321",Role.RESPONDER);

        User user = new User(1L, "John", "Doe", "john@example.com", "@12oldPassword", "+123456789", Role.RESPONDER, true,
                new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");

        Mockito.when(userRepository.findByEmailIdAndInActive(Mockito.anyString(), Mockito.eq(true))).thenReturn(user);
        Mockito.when(authenticationService.getCurrentUserName()).thenReturn("admin");

        Boolean result = userService.ifExistReactivateUser(userCreationDTO);

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));

        Assertions.assertTrue(result);
        Assertions.assertFalse(user.getInActive());
        Assertions.assertEquals("admin", user.getUpdatedBy());
    }
}