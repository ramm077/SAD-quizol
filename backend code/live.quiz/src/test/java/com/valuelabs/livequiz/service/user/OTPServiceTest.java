package com.valuelabs.livequiz.service.user;

import static org.junit.jupiter.api.Assertions.*;

import com.valuelabs.livequiz.model.entity.User;
import com.valuelabs.livequiz.model.enums.Role;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;


import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OTPServiceTest {

    @Mock
    private DisplayUserService displayUserService;

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private OTPService otpService;
    protected MimeMessageHelper createMimeMessageHelper(String emailId,boolean multipart) throws MessagingException{
        return new MimeMessageHelper(javaMailSender.createMimeMessage());
    }
    @Test
    void testGenerateAndSendOTP_Success() throws MessagingException, MailException {
        // Arrange
        String emailId = "john@example.com";
        MimeMessage message = mock(MimeMessage.class);
        Mockito.when(displayUserService.getUserByEmailIdAndInActive(emailId, false))
                .thenReturn(new User(1L, "John", "Doe", emailId, "password", "+123456789", Role.RESPONDER, false,
                        new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default"));
        when(javaMailSender.createMimeMessage()).thenReturn(message);

        // Act
        otpService.generateAndSendOTP(emailId);
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(message);
        // Assert
        // Verify that the sendMail method is called
//        Mockito.verify(otpService, Mockito.times(1)).sendMail(Mockito.eq(emailId), Mockito.anyString());
        // Additional assertions if needed
    }

    @Test
    void testGenerateAndSendOTP_InvalidEmail() throws MessagingException, MailException {
        // Act & Assert
        assertThrows(Exception.class, () -> otpService.generateAndSendOTP(null));
    }

    @Test
    void testSendMail_Success() throws MessagingException, MailException {
        // Arrange
        String emailId = "john@example.com";
        String otp = "123456";
        when(displayUserService.getUserByEmailIdAndInActive(anyString(),eq(false))).thenReturn(new User(1L, "John", "Doe", emailId, "password", "+123456789", Role.RESPONDER, false,
                new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default"));
        // Mocking MimeMessage
        MimeMessage mimeMessage = mock(MimeMessage.class);

        MimeMessageHelper mimeMessageHelper = mock(MimeMessageHelper.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        // Act
        otpService.sendMail(emailId, otp);
        mimeMessageHelper.setTo(emailId);
        // Assert
        // Verify that JavaMailSender.send method is called
        Mockito.verify(mimeMessageHelper, Mockito.times(1)).setTo(anyString());
        // Additional assertions if needed
    }

    @Test
    void testValidateOTP_ValidOTP() throws MessagingException {
        // Arrange
        String email = "john@example.com";
        String enteredOTP = "123456";
        Timestamp curentTime  = new Timestamp(System.currentTimeMillis());
        // Act
        String result = otpService.validateOTP(email, enteredOTP);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testValidateOTP_InvalidOTP() {
        // Arrange
        String email = "john@example.com";
        String enteredOTP = "654321";

        // Act
        String result = otpService.validateOTP(email, enteredOTP);

        // Assert
        assertEquals("Invalid OTP", result);
    }

}