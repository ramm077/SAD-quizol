package com.valuelabs.livequiz.service.user;

import com.valuelabs.livequiz.model.dto.response.DisplayUserDTO;
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

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DisplayUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DisplayUserService displayUserService;

    @Test
    void testGetUserByIdAndInActive() {
        Long userId = 1L;

        User user = new User(userId, "John", "Doe", "john@example.com", "password", "+123456789", Role.RESPONDER, false,
                new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");

        Mockito.when(userRepository.findByUserIdAndInActive(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(user));

        User retrievedUser = displayUserService.getUserByIdAndInActive(userId, false);

        Assertions.assertNotNull(retrievedUser);
        Assertions.assertEquals(userId, retrievedUser.getUserId());
        Assertions.assertEquals("John", retrievedUser.getFirstName());
        Assertions.assertEquals("Doe", retrievedUser.getLastName());
        Assertions.assertEquals("john@example.com", retrievedUser.getEmailId());
        Assertions.assertEquals("+123456789", retrievedUser.getPhoneNumber());
        Assertions.assertEquals(Role.RESPONDER, retrievedUser.getRole());
    }

    @Test
    void testGetUserByEmailIdAndInActive() {
        String emailId = "john@example.com";

        User user = new User(1L, "John", "Doe", emailId, "password", "+123456789", Role.RESPONDER, false,
                new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");

        Mockito.when(userRepository.findByEmailIdAndInActive(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(user);

        User retrievedUser = displayUserService.getUserByEmailIdAndInActive(emailId, false);

        Assertions.assertNotNull(retrievedUser);
        Assertions.assertEquals("John", retrievedUser.getFirstName());
        Assertions.assertEquals("Doe", retrievedUser.getLastName());
        Assertions.assertEquals(emailId, retrievedUser.getEmailId());
        Assertions.assertEquals("+123456789", retrievedUser.getPhoneNumber());
        Assertions.assertEquals(Role.RESPONDER, retrievedUser.getRole());
    }

    @Test
    void testFindAllUsers() {
        Role role = Role.RESPONDER;

        User user1 = new User(1L, "John", "Doe", "john@example.com", "password", "+123456789", role, false,
                new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");

        User user2 = new User(2L, "Jane", "Doe", "jane@example.com", "password", "+987654321", role, false,
                new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");

        Mockito.when(userRepository.findAllByRoleAndInActive(Mockito.any(Role.class), Mockito.anyBoolean()))
                .thenReturn(List.of(user1, user2));

        List<User> userList = displayUserService.findAllUsers(role, false);

        Assertions.assertNotNull(userList);
        Assertions.assertEquals(2, userList.size());
        Assertions.assertEquals("John", userList.get(0).getFirstName());
        Assertions.assertEquals("Jane", userList.get(1).getFirstName());
    }

    @Test
    void testDisplayUserDetails() {
        Long userId = 1L;

        User user = new User(userId, "John", "Doe", "john@example.com", "password", "+123456789", Role.RESPONDER, false,
                new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");

        Mockito.when(userRepository.findByUserIdAndInActive(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(Optional.of(user));

        DisplayUserDTO displayUserDTO = displayUserService.displayUserDetails(userId, false);

        Assertions.assertNotNull(displayUserDTO);
        Assertions.assertEquals(userId, displayUserDTO.userId());
        Assertions.assertEquals("John", displayUserDTO.firstName());
        Assertions.assertEquals("Doe", displayUserDTO.lastName());
        Assertions.assertEquals("john@example.com", displayUserDTO.emailId());
        Assertions.assertEquals("+123456789", displayUserDTO.phoneNumber());
        Assertions.assertEquals(Role.RESPONDER, displayUserDTO.role());
    }

    @Test
    void testGetUserListByIdsAndInactive() {
        List<Long> userIdList = List.of(1L, 2L);

        User user1 = new User(1L, "John", "Doe", "john@example.com", "password", "+123456789", Role.RESPONDER, false,
                new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");

        User user2 = new User(2L, "Jane", "Doe", "jane@example.com", "password", "+987654321", Role.RESPONDER, false,
                new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");

        Mockito.when(userRepository.findByUserIdAndInActive(Mockito.anyLong(), Mockito.eq(false))).thenReturn(Optional.of(user1), Optional.of(user2));

        List<User> userList = displayUserService.getUserListByIdsAndInactive(userIdList, false);

        Assertions.assertNotNull(userList);
        Assertions.assertEquals(2, userList.size());
        Assertions.assertEquals("John", userList.get(0).getFirstName());
        Assertions.assertEquals("Jane", userList.get(1).getFirstName());
    }
}