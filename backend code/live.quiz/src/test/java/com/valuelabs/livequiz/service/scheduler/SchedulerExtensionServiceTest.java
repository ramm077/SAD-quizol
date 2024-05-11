package com.valuelabs.livequiz.service.scheduler;

import com.valuelabs.livequiz.model.dto.request.DraftScheduleDTO;
import com.valuelabs.livequiz.model.entity.*;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.model.enums.Role;
import com.valuelabs.livequiz.service.quiz.DisplayQuizService;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import com.valuelabs.livequiz.service.user.DisplayUserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;

import com.valuelabs.livequiz.repository.SchedulerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerExtensionServiceTest {

    @Mock
    private DisplayQuizService quizService;

    @Mock
    private SchedulerRepository schedulerRepository;

    @Mock
    private DisplayUserService userService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private SchedulerExtensionService schedulerExtensionService;

    @Test
    void testCreateDefaultSchedule() {
        Option option =new Option(65L,"Good",true,"Default");
        Question question = new Question(65L,"How are you", QuestionType.SINGLE, List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(65L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);

        // Mock behavior of quizService
        when(quizService.getQuizById(65L, false)).thenReturn(quiz);

        // Mock behavior of schedulerRepository
        when(schedulerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method to be tested
        Scheduler result = schedulerExtensionService.createDefaultSchedule(new DraftScheduleDTO("2024-02-30 10:00:00", "2024-02-30 12:00:00",65L));

        // Verify that the repository method was called
        verify(schedulerRepository).save(any(Scheduler.class));

        // Check the result
        assertNotNull(result);
    }

    @Test
    void testUpdateScheduleWithTime() {
        User user76=new User(76L,"User1","user1","user45@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        User user77=new User(77L,"User2","user2","user46@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");

        Option option =new Option(76L,"Good",true,"Default");
        Question question = new Question(76L,"How are you", QuestionType.SINGLE, List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(76L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler1 =new Scheduler(76L,quiz, new ArrayList<>(Arrays.asList(user76,user77)),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);


        // Mock behavior of schedulerRepository
        when(schedulerRepository.findBySchedulerIdAndInActive(76L, false)).thenReturn(Optional.of(scheduler1));
        when(schedulerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method to be tested
        boolean result = schedulerExtensionService.updateScheduleWithTime(76L, "2024-03-30 10:00:00", "2024-03-30 12:00:00");

        // Verify that the repository method was called
        verify(schedulerRepository).save(any(Scheduler.class));

        // Check the result
        assertTrue(result);
    }

    @Test
    void testUpdateScheduleWithPerson() {
        User user79=new User(79L,"User2","user2","user79@gmail.com","User@123","8754635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        User user80=new User(80L,"User2","user2","user80@gmail.com","User@123","6554635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");

        Option option =new Option(79L,"Good",true,"Default");
        Question question = new Question(79L,"How are you", QuestionType.SINGLE, List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(79L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler1 =new Scheduler(79L,quiz, new ArrayList<>(List.of(user79)),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);

        // Mock behavior of userService
//        when(userService.getUserByIdAndInActive(79L, false)).thenReturn(user79);
        when(userService.getUserByIdAndInActive(80L, false)).thenReturn(user80);

        // Mock behavior of schedulerRepository
        when(schedulerRepository.findBySchedulerIdAndInActive(79L, false)).thenReturn(Optional.of(scheduler1));
        when(schedulerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method to be tested
        boolean result = schedulerExtensionService.updateScheduleWithPerson(79L, Collections.singletonList(80L));

        // Verify that the repository method was called
        verify(schedulerRepository).save(any(Scheduler.class));

        // Check the result
        assertTrue(result);
    }

    @Test
    void testUpdateScheduleWithNewTimeAndPersons() {
        User user81=new User(81L,"User2","user2","user81@gmail.com","User@123","8754635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        User user82=new User(82L,"User2","user2","user82@gmail.com","User@123","6554635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");

        Option option =new Option(81L,"Good",true,"Default");
        Question question = new Question(81L,"How are you", QuestionType.SINGLE, List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(81L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler1 =new Scheduler(81L,quiz, new ArrayList<>(List.of(user81)),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);


        // Mock behavior of userService and schedulerRepository
        when(userService.getUserByIdAndInActive(82L, false)).thenReturn(user82);
        when(schedulerRepository.findBySchedulerIdAndInActive(81L, false)).thenReturn(Optional.of(scheduler1));
        when(schedulerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method to be tested
        boolean result = schedulerExtensionService.updateScheduleWithNewTimeAndPersons(81L, Collections.singletonList(82L), "2024-04-30 10:00:00", "2024-04-30 12:00:00", "user");

        // Verify that the repository method was called
        verify(schedulerRepository, times(2)).save(any(Scheduler.class));

        // Check the result
        assertTrue(result);
    }

    @Test
    void testUpdateScheduleWithNewTimeAndPersons_Exception() {
        User user83=new User(83L,"User2","user2","user83@gmail.com","User@123","8754635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        User user84=new User(84L,"User2","user2","user84@gmail.com","User@123","6554635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");

        Option option =new Option(83L,"Good",true,"Default");
        Question question = new Question(83L,"How are you", QuestionType.SINGLE, List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(83L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler1 =new Scheduler(83L,quiz, new ArrayList<>(List.of(user83)),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);
        // Mock behavior of userService and schedulerRepository to simulate an exception
        when(userService.getUserByIdAndInActive(84L, false)).thenReturn(user84);
        when(schedulerRepository.findBySchedulerIdAndInActive(83L, false)).thenReturn(Optional.of(scheduler1));
        when(schedulerRepository.save(any())).thenThrow(new RuntimeException("Simulated exception"));

        // Call the method to be tested and expect an exception
        assertThrows(InvalidResourceDetailsException.class, () ->
                schedulerExtensionService.updateScheduleWithNewTimeAndPersons(83L, Collections.singletonList(84L), "2024-05-30 10:00:00", "2024-05-30 12:00:00", "user")
        );

        // Verify that the repository method was called
        verify(schedulerRepository, atLeastOnce()).save(any(Scheduler.class));
    }

    @Test
    void testUpdateScheduleWithTime_Exception() {
        User user85=new User(85L,"User2","user2","user85@gmail.com","User@123","9754635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        Option option =new Option(85L,"Good",true,"Default");
        Question question = new Question(85L,"How are you", QuestionType.SINGLE, List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(85L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler12 =new Scheduler(85L,quiz, new ArrayList<>(List.of(user85)),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);

        // Mock behavior of schedulerRepository to simulate an exception
        when(schedulerRepository.findBySchedulerIdAndInActive(85L, false)).thenReturn(Optional.of(scheduler12));
        when(schedulerRepository.save(any())).thenThrow(new RuntimeException("Simulated exception"));

        // Call the method to be tested and expect an exception
        assertThrows(InvalidResourceDetailsException.class, () ->
                schedulerExtensionService.updateScheduleWithTime(85L, "2024-06-30 10:00:00", "2024-06-30 12:00:00")
        );

        // Verify that the repository method was called
        verify(schedulerRepository).save(any(Scheduler.class));
    }

    // Additional tests may be required for exception scenarios, etc.
}