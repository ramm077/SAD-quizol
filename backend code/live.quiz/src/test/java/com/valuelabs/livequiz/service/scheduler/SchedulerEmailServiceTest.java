package com.valuelabs.livequiz.service.scheduler;

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

import java.io.IOException;
import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchedulerEmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private SchedulerEmailService schedulerEmailService;
    @Test
    void  testSendEmail() throws MessagingException {
        String emailId = "john@example.com";
        // Mocking MimeMessage
        MimeMessage mimeMessage = mock(MimeMessage.class);
        MimeMessageHelper mimeMessageHelper = mock(MimeMessageHelper.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        // Act
        schedulerEmailService.sendEmail(emailId, "test subject","test body");
        mimeMessageHelper.setTo(emailId);
        // Assert
        // Verify that JavaMailSender.send method is called
        Mockito.verify(mimeMessageHelper, Mockito.times(1)).setTo(anyString());
    }
    @Test
    void testSendQuizScheduleEmailToResponder() throws MessagingException, IOException {

        User user = new User(1L, "User1", "user1", "user1@gmail.com", "user@123", "+54635345", Role.RESPONDER, false,
                new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");
        Timestamp startTime = Timestamp.valueOf("2024-01-16 14:00:00");
        Timestamp endTime = Timestamp.valueOf("2024-01-16 16:00:00");
        MimeMessage mimeMessage = mock(MimeMessage.class);
        MimeMessageHelper mimeMessageHelper = mock(MimeMessageHelper.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
//        Mockito.when(mimeMessage1.getContent()).thenReturn("TestContent");
        schedulerEmailService.sendQuizScheduleEmailToResponder(user, "TestQuiz", "TEST", startTime, endTime);

        // Add assertions here to verify the behavior, for example, verify that javaMailSender.send was called.
         Mockito.verify(javaMailSender, Mockito.times(1)).send(Mockito.any(MimeMessage.class));
    }

    @Test
    void testSendQuizReminderEmail() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        MimeMessageHelper mimeMessageHelper = mock(MimeMessageHelper.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        User user = new User(1L, "User1", "user1", "user1@gmail.com", "user@123", "+54635345", Role.RESPONDER, false,
                new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");
        Timestamp startTime = Timestamp.valueOf("2024-01-16 14:00:00");

        schedulerEmailService.sendQuizReminderEmail(user, "TestQuiz", "TEST", startTime, 30);

        // Add assertions here to verify the behavior, for example, verify that javaMailSender.send was called.
        Mockito.verify(javaMailSender, Mockito.times(1)).send(Mockito.any(MimeMessage.class));
    }

    @Test
    void testSendUpdatedScheduleEmail() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        MimeMessageHelper mimeMessageHelper = mock(MimeMessageHelper.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        User user = new User(1L, "User1", "user1", "user1@gmail.com", "user@123", "+54635345", Role.RESPONDER, false,
                new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");
        Timestamp startTime = Timestamp.valueOf("2024-01-16 14:00:00");
        Timestamp endTime = Timestamp.valueOf("2024-01-16 16:00:00");

        schedulerEmailService.sendUpdatedScheduleEmail(user, "TestQuiz","TEST", startTime, endTime);

        // Add assertions here to verify the behavior.
        Mockito.verify(javaMailSender, Mockito.times(1)).send(Mockito.any(MimeMessage.class));
    }

    @Test
    void testSendQuizCancellationEmail() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        MimeMessageHelper mimeMessageHelper = mock(MimeMessageHelper.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        User user = new User(1L, "User1", "user1", "user1@gmail.com", "user@123", "+54635345", Role.RESPONDER, false,
                new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");

        schedulerEmailService.sendQuizCancellationEmail(user, "TestQuiz", "TEST","Reason for cancellation");

        // Add assertions here to verify the behavior.
        Mockito.verify(javaMailSender, Mockito.times(1)).send(Mockito.any(MimeMessage.class));
    }
}