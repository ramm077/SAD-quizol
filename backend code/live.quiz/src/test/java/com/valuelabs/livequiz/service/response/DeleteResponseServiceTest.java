package com.valuelabs.livequiz.service.response;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.entity.*;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.model.enums.Role;
import com.valuelabs.livequiz.service.scheduler.CreateSchedulerService;
import com.valuelabs.livequiz.service.user.DisplayUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeleteResponseServiceTest {
    @Mock
    private DisplayUserService userService;
    @Mock
    private CreateSchedulerService schedulerService;
    @Mock
    private DisplayResponseService displayResponseService;
    @Mock
    private ResponseCaptureService responseCaptureService;
    @InjectMocks
    private DeleteResponseService deleteResponseService;

    @Test
    public void testDeleteResponsesOfUserInQuiz_throwsResourceNotFoundException(){
        User user = new User(1L,"Chandra","Mouli","chandramoulikodidasu@gmail.com","Pass@123","8074703740", Role.RESPONDER,false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null);

        Long userId = 1L;
        Question question = new Question(1L,"Question", QuestionType.TEXT,null,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Scheduler scheduler = new Scheduler(1L,new Quiz(1L,"SQL", QuizType.OPEN_ENDED,List.of(question)),List.of(user),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-24 12:30:45.123456789"),Timestamp.valueOf("2024-01-25 12:30:45.123456789"),null,false);
        Long schedulerId = 1L;
        when(userService.getUserByIdAndInActive(userId,false)).thenReturn(user);
        when(schedulerService.getScheduleById(1L)).thenReturn(scheduler);
        when(displayResponseService.getResponse(user,scheduler,question)).thenThrow(ResourceNotFoundException.class);
        ResourceNotFoundException exception1 = assertThrows(ResourceNotFoundException.class, () -> deleteResponseService.deleteResponsesOfUserInQuiz(userId,schedulerId));
    }

    @Test
    public void testDeleteResponsesOfUserInQuiz_SuccessfulDeletion(){
        User user = new User(1L,"Chandra","Mouli","chandramoulikodidasu@gmail.com","Pass@123","8074703740", Role.RESPONDER,false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null);

        Long userId = 1L;
        Question question = new Question(1L,"Question", QuestionType.TEXT,null,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Scheduler scheduler = new Scheduler(1L,new Quiz(1L,"SQL", QuizType.OPEN_ENDED,List.of(question)),List.of(user),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-24 12:30:45.123456789"),Timestamp.valueOf("2024-01-25 12:30:45.123456789"),null,false);
        Long schedulerId = 1L;
        when(userService.getUserByIdAndInActive(userId,false)).thenReturn(user);
        when(schedulerService.getScheduleById(userId)).thenReturn(scheduler);
        Response response = new Response(1L,question,new TextResponse("answer",user.getUserName()),null,user,scheduler,-1,new Timestamp(System.currentTimeMillis()),new Timestamp(System.currentTimeMillis()),false,user.getUserName(),null,true,false);
        when(displayResponseService.getResponse(user,scheduler,question)).thenReturn(response);
        when(responseCaptureService.saveResponse(response)).thenReturn(response);
        deleteResponseService.deleteResponsesOfUserInQuiz(userId,schedulerId);
        assertEquals(true,response.getInActive());
    }
}