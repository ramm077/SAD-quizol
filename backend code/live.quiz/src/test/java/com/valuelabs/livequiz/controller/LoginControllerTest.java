package com.valuelabs.livequiz.controller;

import com.valuelabs.livequiz.model.dto.request.ForgotPasswordDTO;
import com.valuelabs.livequiz.model.dto.request.LoginDTO;
import com.valuelabs.livequiz.model.dto.request.UserCreationDTO;
import com.valuelabs.livequiz.model.dto.request.ValidateOTPDTO;
import com.valuelabs.livequiz.model.dto.response.ErrorStatusDTO;
import com.valuelabs.livequiz.model.dto.response.JWTAuthResponse;
import com.valuelabs.livequiz.model.entity.User;
import com.valuelabs.livequiz.model.enums.Role;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import com.valuelabs.livequiz.service.user.OTPService;
import com.valuelabs.livequiz.service.user.UserService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthControllerTests {
    @Mock
    private UserService userService;

    @Mock
    private OTPService otpService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testLogin() {
        // Mocking
        LoginDTO loginDTO = new LoginDTO("testuser", "password");
        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse("token",1L,"test","user","test@example.com",Role.RESPONDER);
        Mockito.when(authenticationService.login(loginDTO)).thenReturn(new JWTAuthResponse("token",1L,"test","user","test@example.com",Role.RESPONDER));

        // Test
        ResponseEntity<?> responseEntity = authController.login(loginDTO);

        // Assertions
        Mockito.verify(authenticationService, Mockito.times(1)).login(loginDTO);
        assert responseEntity.getStatusCode() == HttpStatus.CREATED;
        assert responseEntity.getBody().equals(jwtAuthResponse);
    }

    @Test
    void testCreatePerson() {
        // Mocking
        UserCreationDTO userCreationDTO = new UserCreationDTO("test", "user","test@example.com" ,"password", "1234567890",Role.RESPONDER);
        Mockito.when(userService.ifExistReactivateUser(userCreationDTO)).thenReturn(false);
        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse("testToken",1L,"test","user","test@example.com", Role.RESPONDER);
        Mockito.when(authenticationService.defaultUserCreation(userCreationDTO)).thenReturn(new User());

        // Test
        ResponseEntity<?> responseEntity = authController.createPerson(userCreationDTO);

        // Assertions
        Mockito.verify(userService, Mockito.times(1)).ifExistReactivateUser(userCreationDTO);
        Mockito.verify(authenticationService, Mockito.times(1)).defaultUserCreation(userCreationDTO);
        assert responseEntity.getStatusCode() == HttpStatus.CREATED;
        assert responseEntity.getBody().equals(new User());
    }

    @Test
    void testSendOTP() throws MessagingException {
        // Mocking
        ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO("test@example.com");
        Mockito.doNothing().when(otpService).generateAndSendOTP(forgotPasswordDTO.emailId());

        // Test
        ResponseEntity<?> responseEntity = authController.sendOTP(forgotPasswordDTO);

        // Assertions
        Mockito.verify(otpService, Mockito.times(1)).generateAndSendOTP(forgotPasswordDTO.emailId());
        assert responseEntity.getStatusCode() == HttpStatus.CREATED;
        assert responseEntity.getBody().equals(new ErrorStatusDTO("OTP sent successfully to " + forgotPasswordDTO.emailId()));
    }

    @Test
    void testVerifyValidOTP() {
        // Mocking
        ValidateOTPDTO validateOTPDTO = new ValidateOTPDTO("test@example.com", "123456");
        Mockito.when(otpService.validateOTP(validateOTPDTO.emailId(), validateOTPDTO.enteredOTP())).thenReturn("Valid OTP");

        // Test
        ResponseEntity<?> responseEntity = authController.verifyValidOTP(validateOTPDTO);

        // Assertions
        Mockito.verify(otpService, Mockito.times(1)).validateOTP(validateOTPDTO.emailId(), validateOTPDTO.enteredOTP());
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody().equals(new ErrorStatusDTO("Valid OTP"));
    }

    @Test
    void testChangePassword() {
        // Mocking
        LoginDTO loginDTO = new LoginDTO("test@example.com", "newPassword");
        Mockito.doNothing().when(userService).changePassword(loginDTO);

        // Test
        ResponseEntity<?> responseEntity = authController.changePassword(loginDTO);

        // Assertions
        Mockito.verify(userService, Mockito.times(1)).changePassword(loginDTO);
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody().equals(new ErrorStatusDTO("changed password"));
    }
    @Test
    void testVerifyValidOTP_TimeLimitExceeded(){
        ValidateOTPDTO validateOTPDTOt = new ValidateOTPDTO("sumit@gmail.com","@Welcome123");
        Mockito.when(otpService.validateOTP(validateOTPDTOt.emailId(),validateOTPDTOt.enteredOTP())).thenReturn("Time limit exceeded. Please request a new OTP.");
        ResponseEntity<?> responseEntity = authController.verifyValidOTP(validateOTPDTOt);
        assert responseEntity.getStatusCode() == HttpStatus.REQUEST_TIMEOUT;
    }
    @Test
    void testVerifyValidOTP_Unauthorized(){
        ValidateOTPDTO validateOTPDTOt = new ValidateOTPDTO("MohanDas@gmail.com","@Welcome1234");
        Mockito.when(otpService.validateOTP(validateOTPDTOt.emailId(),validateOTPDTOt.enteredOTP())).thenReturn("Invalid OTP.");
        ResponseEntity<?> responseEntity = authController.verifyValidOTP(validateOTPDTOt);
        assert responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED;

    }
    @Test
    void createPerson_AlreadyExistsButInactive(){
        UserCreationDTO userCreationDTOe = new UserCreationDTO("Steven","Hawking","steven@gmail.com","@Welocme1235","6323324536",Role.RESPONDER);
        Mockito.when(userService.ifExistReactivateUser(userCreationDTOe)).thenReturn(true);
        ResponseEntity<?> responseEntity = authController.createPerson(userCreationDTOe);
        assert responseEntity.getStatusCode() == HttpStatus.OK;

    }
}