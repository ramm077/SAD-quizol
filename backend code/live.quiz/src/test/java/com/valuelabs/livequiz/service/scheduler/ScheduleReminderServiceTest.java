package com.valuelabs.livequiz.service.scheduler;

import com.valuelabs.livequiz.model.entity.*;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.model.enums.Role;
import org.assertj.core.util.Arrays;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

@ExtendWith(MockitoExtension.class)
class ScheduleReminderServiceTest {

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private SchedulerEmailService schedulerEmailService;

    @InjectMocks
    private ScheduleReminderService scheduleReminderService;

    // Test the scheduleSendCreateScheduler method
    @Test
    void testScheduleSendCreateScheduler() {
        // Mocking
        Scheduler scheduler = new Scheduler();
        Instant currentTime = Instant.now();

        // Test
        scheduleReminderService.scheduleSendCreateScheduler(scheduler);

        // Assertions
        Mockito.verify(taskScheduler, Mockito.times(1)).schedule(Mockito.any(), Mockito.eq(currentTime));
    }

    // Test the scheduleSendCancelScheduler method
    @Test
    void testScheduleSendCancelScheduler() {
        // Mocking
        Scheduler scheduler = new Scheduler();
        Instant currentTime = Instant.now();
        String reason = "Cancellation Reason";

        // Test
        scheduleReminderService.scheduleSendCancelScheduler(scheduler, reason);

        // Assertions
        Mockito.verify(taskScheduler, Mockito.times(1)).schedule(Mockito.any(), Mockito.eq(currentTime));
    }

    // Test the scheduleSendUpdateScheduler method
    @Test
    void testScheduleSendUpdateScheduler() {
        // Mocking
        Scheduler scheduler = new Scheduler();
        List<User> existingUsers = new ArrayList<>();
        List<User> addUsers = new ArrayList<>();
        List<User> deleteUsers = new ArrayList<>();
        Instant currentTime = Instant.now();

        // Test
        scheduleReminderService.scheduleSendUpdateScheduler(existingUsers, addUsers, deleteUsers, scheduler);

        // Assertions
        Mockito.verify(taskScheduler, Mockito.times(1)).schedule(Mockito.any(), Mockito.eq(currentTime));
    }

    // Test the scheduleReminders method
    @Test
    void testScheduleReminders() {
        // Mocking
        Scheduler scheduler = new Scheduler();
        scheduler.setStartTime(Timestamp.from(Instant.now().plus(Duration.ofMinutes(45))));
        int reminderTime = 45;

        // Test
        scheduleReminderService.scheduleReminders(scheduler);

        // Assertions
        Mockito.verify(taskScheduler, Mockito.times(1)).schedule(Mockito.any(), Mockito.any(Instant.class));
    }

    // Test the sendRemindersEmailToResponders method
    @Test
    void testSendRemindersEmailToResponders() {
        // Mocking
        User user1=new User(1L,"User1","user1","user1@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        Option option =new Option(1L,"Good",true,"Default");
        Question question = new Question(1L,"How are you", QuestionType.SINGLE,List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(1L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler1 =new Scheduler(1L,quiz,List.of(user1),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);

        // Test
        scheduleReminderService.sendRemindersEmailToResponders(scheduler1, 10);

        // Assertions
        Mockito.verify(schedulerEmailService, Mockito.times(1)).sendQuizReminderEmail(Mockito.any(User.class), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.eq(10));
    }

    // Test the sendQuizScheduledEmailToResponders method
    @Test
    void testSendQuizScheduledEmailToResponders() {
        // Mocking
        User user1=new User(1L,"User1","user1","user1@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        Option option =new Option(1L,"Good",true,"Default");
        Question question = new Question(1L,"How are you", QuestionType.SINGLE,List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(1L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler1 =new Scheduler(1L,quiz,List.of(user1),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);
        // Test
        scheduleReminderService.sendQuizScheduledEmailToResponders(scheduler1);

        // Assertions
        Mockito.verify(schedulerEmailService, Mockito.times(1)).sendQuizScheduleEmailToResponder(Mockito.any(User.class), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    // Test the sendUpdatedQuizScheduledEmailToResponders method
    @Test
    void testSendUpdatedQuizScheduledEmailToResponders() {
        // Mocking
        User user1=new User(1L,"User1","user1","user1@gmail.com","User@123","1254635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        User user2=new User(2L,"User2","user2","user2@gmail.com","User@123","2354635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        User user3=new User(3L,"User3","user3","user3@gmail.com","User@123","3454635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        Option option =new Option(1L,"Good",true,"Default");
        Question question = new Question(1L,"How are you", QuestionType.SINGLE,List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(1L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler1 =new Scheduler(1L,quiz,List.of(user1,user2),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);

        // Mocking
        List<User> existingUsers = new ArrayList<>(List.of(user1,user2));
        List<User> addUsers = new ArrayList<>(List.of(user3));
        List<User> deleteUsers = new ArrayList<>(List.of(user2));

        // Test
        scheduleReminderService.sendUpdatedQuizScheduledEmailToResponders(existingUsers, addUsers, deleteUsers, scheduler1);

        // Assertions
        Mockito.verify(schedulerEmailService, Mockito.times(existingUsers.size())).sendUpdatedScheduleEmail(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(schedulerEmailService, Mockito.times(addUsers.size())).sendQuizScheduleEmailToResponder(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(schedulerEmailService, Mockito.times(deleteUsers.size())).sendQuizCancellationEmail(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    // Test the sendQuizCancellationEmailToResponders method
    @Test
    void testSendQuizCancellationEmailToResponders() {
        // Mocking
        // Mocking
        User user1=new User(1L,"User1","user1","user1@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        Option option =new Option(1L,"Good",true,"Default");
        Question question = new Question(1L,"How are you", QuestionType.SINGLE,List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(1L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler1 =new Scheduler(1L,quiz,List.of(user1),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);

        // Test
        scheduleReminderService.sendQuizCancellationEmailToResponders(scheduler1, "Cancellation Reason");

        // Assertions
        Mockito.verify(schedulerEmailService, Mockito.times(1)).sendQuizCancellationEmail(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }
    @Test
    public void testCancelReminderTask() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.initialize();
        ScheduledFuture<?> mockScheduledTask = threadPoolTaskScheduler.schedule(()->{}, Instant.now().plusSeconds(1));
        Long schedulerId =25L;
        User user1=new User(25L,"User1","user1","user25@gmail.com","User@123","5463534591", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        Option option =new Option(25L,"Good",true,"Default");
        Question question = new Question(25L,"How are you", QuestionType.SINGLE,List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(25L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler125 =new Scheduler(25L,quiz,List.of(user1),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);
        Mockito.doReturn((ScheduledFuture<?>) mockScheduledTask).when(taskScheduler).schedule(Mockito.any(Runnable.class),Mockito.any(Instant.class));
        scheduleReminderService.scheduleReminders(scheduler125);
//        Assert.assertTrue(scheduleReminderService.get());
        scheduleReminderService.cancelReminderTask(schedulerId);
        Mockito.verify(taskScheduler).schedule(Mockito.any(Runnable.class),Mockito.any(Instant.class));
    }


}