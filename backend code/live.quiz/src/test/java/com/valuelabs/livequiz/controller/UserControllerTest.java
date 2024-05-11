package com.valuelabs.livequiz.controller;

import com.valuelabs.livequiz.model.dto.request.UpdateUserDTO;
import com.valuelabs.livequiz.model.dto.response.DisplayUserDTO;
import com.valuelabs.livequiz.model.enums.Role;
import com.valuelabs.livequiz.service.user.DisplayUserService;
import com.valuelabs.livequiz.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class UserControllerTests {
    @Mock
    private UserService userService;
    @Mock
    private DisplayUserService displayUserService;
    @InjectMocks
    private UserController userController;
    @Test
    void testGetAllActiveUsers() {
        // Mocking
        DisplayUserDTO displayUserDTO = new DisplayUserDTO(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "+123456789",
                Role.RESPONDER
        );
        Mockito.when(displayUserService.displayAllUsers(Role.RESPONDER, false)).thenReturn(List.of(displayUserDTO));

        // Test
        ResponseEntity<?> responseEntity = userController.getAllActiveUsers();

        // Assertions
        Mockito.verify(displayUserService, Mockito.times(1)).displayAllUsers(Role.RESPONDER, false);
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody().equals(List.of(displayUserDTO));
    }
    @Test
    void testGetUserDetailsById() {
        DisplayUserDTO displayUserDTO = new DisplayUserDTO(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "+123456789",
                Role.RESPONDER
        );
        // Mocking
        Long userId = 1L;
        Mockito.when(displayUserService.displayUserDetails(userId, false)).thenReturn(displayUserDTO);

        // Test
        ResponseEntity<?> responseEntity = userController.getUserDetailsById(userId);

        // Assertions
        Mockito.verify(displayUserService, Mockito.times(1)).displayUserDetails(userId, false);
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody().equals(displayUserDTO);
    }
    @Test
    void testUpdatePersonInfo() {
        // Mocking
        Long userId = 1L;
        UpdateUserDTO updateUserDTO = new UpdateUserDTO("UpdatedFirstName", "UpdatedLastName", "+987654321");
        Mockito.when(userService.updateUserById(userId, updateUserDTO)).thenReturn(true);

        // Test
        ResponseEntity<?> responseEntity = userController.updatePersonInfo(userId, updateUserDTO);

        // Assertions
        Mockito.verify(userService, Mockito.times(1)).updateUserById(userId, updateUserDTO);
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert Objects.equals(responseEntity.getBody(), "Person updated with the given details");
    }
    @Test
    void testUpdatePersonInfoNotFound() {
        // Mocking
        Long userId = 1L;
        UpdateUserDTO updateUserDTO = new UpdateUserDTO("UpdatedFirstName", "UpdatedLastName", "+987654321"
        );
        Mockito.when(userService.updateUserById(userId, updateUserDTO)).thenReturn(false);

        // Test
        ResponseEntity<?> responseEntity = userController.updatePersonInfo(userId, updateUserDTO);

        // Assertions
        Mockito.verify(userService, Mockito.times(1)).updateUserById(userId, updateUserDTO);
        assert responseEntity.getStatusCode() == HttpStatus.NOT_FOUND;
        assert responseEntity.getBody().equals("Person Not found with given details");
    }
    @Test
    void testUpdatePersonInfoException() {
        // Mocking
        Long userId = 1L;
        UpdateUserDTO updateUserDTO = new UpdateUserDTO("UpdatedFirstName", "UpdatedLastName", "+987654321"
        );
        Mockito.when(userService.updateUserById(userId, updateUserDTO)).thenThrow(new RuntimeException("Test Exception"));

        // Test
        ResponseEntity<?> responseEntity = userController.updatePersonInfo(userId, updateUserDTO);

        // Assertions
        Mockito.verify(userService, Mockito.times(1)).updateUserById(userId, updateUserDTO);
        assert responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST;
        String body = responseEntity.getBody().toString();
        assert body.startsWith("An error occurred while updating person :");
    }
    @Test
    void testDeletePerson() {
        // Mocking
        Long userId = 1L;
        Mockito.when(userService.deleteActivePersonById(userId)).thenReturn(true);

        // Test
        ResponseEntity<?> responseEntity = userController.deletePerson(userId);

        // Assertions
        Mockito.verify(userService, Mockito.times(1)).deleteActivePersonById(userId);
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody().equals("Person Deleted with the id" + userId);
    }
    @Test
    void testDeletePersonNotFound() {
        // Mocking
        Long userId = 1L;
        Mockito.when(userService.deleteActivePersonById(userId)).thenReturn(false);

        // Test
        ResponseEntity<?> responseEntity = userController.deletePerson(userId);

        // Assertions
        Mockito.verify(userService, Mockito.times(1)).deleteActivePersonById(userId);
        assert responseEntity.getStatusCode() == HttpStatus.NOT_FOUND;
        assert responseEntity.getBody().equals("Person Not found with given details");
    }
    @Test
    void testDeletePersonException() {
        // Mocking
        Long userId = 1L;
        Mockito.when(userService.deleteActivePersonById(userId)).thenThrow(new RuntimeException("Test Exception"));

        // Test
        ResponseEntity<?> responseEntity = userController.deletePerson(userId);

        // Assertions
        Mockito.verify(userService, Mockito.times(1)).deleteActivePersonById(userId);
        assert responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST;
        String body = responseEntity.getBody().toString();
        assert body.startsWith("An error occurred while deleting person :");
    }
}