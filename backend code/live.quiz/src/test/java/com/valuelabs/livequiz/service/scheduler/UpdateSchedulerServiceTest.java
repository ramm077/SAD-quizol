package com.valuelabs.livequiz.service.scheduler;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.model.dto.request.UpdateScheduleRequestDTO;
import com.valuelabs.livequiz.model.entity.*;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.model.enums.Role;
import com.valuelabs.livequiz.repository.SchedulerRepository;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import com.valuelabs.livequiz.service.user.DisplayUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class UpdateSchedulerServiceTest {
    @Mock
    private ScheduleReminderService scheduleReminderService;
    @Mock
    private SchedulerRepository schedulerRepository;
    @Mock
    private DisplayUserService displayUserService;
    @Mock
    private AuthenticationService authenticationService;
    @InjectMocks
    private UpdateSchedulerService updateSchedulerService;
    @Test
    void testUpdateScheduleWithResonSuccess() {
        User user1=new User(1L,"User1","user1","user1@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        User user2=new User(2L,"User2","user2","user2@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        User user3=new User(3L,"User3","user3","user3@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        Option option =new Option(1L,"Good",true,"Default");
        Question question = new Question(1L,"How are you", QuestionType.SINGLE, List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(1L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler1 =new Scheduler(1L,quiz, new ArrayList<>(Arrays.asList(user1,user3)),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);
        List<Long> addPersonIds = new ArrayList<>(Arrays.asList(2L));
        List<Long> deletePersonIds = new ArrayList<>(Arrays.asList(3L));
        Mockito.when(schedulerRepository.findBySchedulerIdAndInActive(1L,false)).thenReturn(Optional.of(scheduler1));
        Mockito.when(displayUserService.getUserListByIdsAndInactive(addPersonIds,false)).thenReturn(List.of(user2));
        Mockito.when(displayUserService.getUserListByIdsAndInactive(deletePersonIds,false)).thenReturn(List.of(user3));
        Boolean result = updateSchedulerService.updateScheduleWithReason(1L,new UpdateScheduleRequestDTO("2024-01-18 14:00:00","2024-01-19 14:00:00",addPersonIds,deletePersonIds,"Some reason"));
        Mockito.verify(schedulerRepository,Mockito.times(1)).findBySchedulerIdAndInActive(Mockito.anyLong(),Mockito.eq(false));
        Mockito.verify(schedulerRepository,Mockito.times(1)).save(Mockito.any(Scheduler.class));
        Mockito.verify(scheduleReminderService,Mockito.times(1)).cancelReminderTask(Mockito.anyLong());
        Mockito.verify(scheduleReminderService,Mockito.times(1)).scheduleSendUpdateScheduler(Mockito.anyList(),Mockito.anyList(),Mockito.anyList(),Mockito.any(Scheduler.class));
    }
    @Test
    void testDeleteScheduleWithResonSuccess() {
        User user1=new User(1L,"User1","user1","user1@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        User user2=new User(2L,"User2","user2","user2@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        Option option =new Option(1L,"Good",true,"Default");
        Question question = new Question(1L,"How are you", QuestionType.SINGLE, List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(1L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler1 =new Scheduler(1L,quiz, new ArrayList<>(Arrays.asList(user1,user2)),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);
        Mockito.when(schedulerRepository.findBySchedulerIdAndInActive(1L,false)).thenReturn(Optional.of(scheduler1));
        Boolean result = updateSchedulerService.deleteScheduleById(1L,"Unavoidable circumstances");
        Mockito.verify(schedulerRepository,Mockito.times(1)).findBySchedulerIdAndInActive(Mockito.anyLong(),Mockito.eq(false));
        Mockito.verify(schedulerRepository,Mockito.times(1)).save(Mockito.any(Scheduler.class));
    }
    @Test
    void testFindScheduleByQuizWhenQuizExists(){
        User user1=new User(1L,"User1","user1","user1@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        User user2=new User(2L,"User2","user2","user2@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        Option option =new Option(1L,"Good",true,"Default");
        Question question = new Question(1L,"How are you", QuestionType.SINGLE, List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(1L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler1 =new Scheduler(1L,quiz, new ArrayList<>(Arrays.asList(user1,user2)),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);

        Mockito.when(schedulerRepository.findAllByQuizAndInActive(quiz,false)).thenReturn(List.of(scheduler1));
        boolean result = updateSchedulerService.findScheduleByQuiz(quiz);
        Mockito.verify(schedulerRepository).findAllByQuizAndInActive(quiz,false);
        Assertions.assertTrue(result);
    }
    @Test
    void testFindScheduleByQuizWhenQuizDoesNotExist(){
        User user45=new User(45L,"User1","user1","user45@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        User user46=new User(46L,"User2","user2","user46@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        Option option =new Option(45L,"Good",true,"Default");
        Question question = new Question(45L,"How are you", QuestionType.SINGLE, List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(45L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler1 =new Scheduler(45L,quiz, new ArrayList<>(Arrays.asList(user45,user46)),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);

        Mockito.when(schedulerRepository.findAllByQuizAndInActive(quiz,false)).thenReturn(Collections.emptyList());
        boolean result = updateSchedulerService.findScheduleByQuiz(quiz);
        Mockito.verify(schedulerRepository).findAllByQuizAndInActive(quiz,false);
        Assertions.assertFalse(result);
    }
    @Test
    void testFindScheduleByQuizWhenQuizIsNull(){
        Assertions.assertThrows(InvalidResourceDetailsException.class,()->{
            updateSchedulerService.findScheduleByQuiz(null);
        });
        Mockito.verifyNoInteractions(schedulerRepository);
    }

}
