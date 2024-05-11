package com.valuelabs.livequiz.service.response;

import static org.junit.jupiter.api.Assertions.*;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.dto.response.*;
import com.valuelabs.livequiz.model.entity.*;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.model.enums.Role;
import com.valuelabs.livequiz.repository.ResponseRepository;
import com.valuelabs.livequiz.service.question.DisplayQuestionService;
import com.valuelabs.livequiz.service.scheduler.CreateSchedulerService;
import com.valuelabs.livequiz.service.user.DisplayUserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisplayResponseServiceTest {

    @Mock
    private ResponseRepository responseRepository;

    @Mock
    private DisplayUserService displayUserService;

    @Mock
    private CreateSchedulerService schedulerService;

    @Mock
    private DisplayQuestionService displayQuestionService;

    @InjectMocks
    private DisplayResponseService displayResponseService;

    @Test
    public void testDisplayResponsesOfUserInSchedule_throwsInvalidResourceDetailsException(){

        Exception exception1 = assertThrows(InvalidResourceDetailsException.class,() -> displayResponseService.displayResponsesOfUserInSchedule(null,null));
        assertEquals("User Id must be provided for displaying responses of a quiz",exception1.getMessage());

        Exception exception2 = assertThrows(InvalidResourceDetailsException.class,() -> displayResponseService.displayResponsesOfUserInSchedule(1L,null));
        assertEquals("Scheduler Id must be provided for displaying responses of a User",exception2.getMessage());

    }

    @Test
    public void testDisplayResponsesOfUserInSchedule_throwsResourceNotFoundException(){
        User user1 = new User(1L,"Chandra","Mouli","chandramoulikodidasu@gmail.com","Pass@123","8074703740", Role.RESPONDER,false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null);
        Option option1 = new Option(1L,"Option",true,"Default");
        Question question1 = new Question(1L,"Question", QuestionType.SINGLE,List.of(option1),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz1 = new Quiz(1L,"SQL", QuizType.TEST,List.of(question1));
        Scheduler scheduler1 = new Scheduler(1L,quiz1,List.of(user1),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-24 12:30:45.123456789"),Timestamp.valueOf("2024-01-25 12:30:45.123456789"),null,false);
        when(schedulerService.getScheduleById(1L)).thenReturn(scheduler1);
        when(displayUserService.getUserByIdAndInActive(1L,false)).thenReturn(user1);
        when(responseRepository.findByUserAndSchedulerAndQuestionAndInActive(user1, scheduler1, question1, false)).thenReturn(null);
        Exception exception1 = assertThrows(ResourceNotFoundException.class,() -> displayResponseService.displayResponsesOfUserInSchedule(1L,1L));
        assertEquals("Response not found for Question " + question1.getQuestionText(),exception1.getMessage());

    }

    @Test
    public void testDisplayResponsesOfUserInSchedule_Success(){
        User user1 = new User(1L,"Chandra","Mouli","chandramoulikodidasu@gmail.com","Pass@123","8074703740", Role.RESPONDER,false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null);
        Option option1 = new Option(1L,"Option",true,"Default");
        Question question1 = new Question(1L,"Question", QuestionType.SINGLE,List.of(option1),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz1 = new Quiz(1L,"SQL", QuizType.TEST,List.of(question1));
        Scheduler scheduler1 = new Scheduler(1L,quiz1,List.of(user1),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-24 12:30:45.123456789"),Timestamp.valueOf("2024-01-25 12:30:45.123456789"),null,false);
        OptionResponse optionResponse1 = new OptionResponse(1L,List.of(option1),new Timestamp(System.currentTimeMillis()),"ChandraMouli",new Timestamp(System.currentTimeMillis()),null,false);
        Response response1 = new Response(1L,question1,null,optionResponse1,user1,scheduler1,1,new Timestamp(System.currentTimeMillis()),new Timestamp(System.currentTimeMillis()),false,"ChandraMouli",null,true,false);
        when(schedulerService.getScheduleById(1L)).thenReturn(scheduler1);
        when(displayUserService.getUserByIdAndInActive(1L,false)).thenReturn(user1);
        when(displayQuestionService.getQuestionById(1L,false)).thenReturn(question1);
        when(responseRepository.findByUserAndSchedulerAndQuestionAndInActive(user1, scheduler1, question1, false)).thenReturn(response1);
        DisplayResponsesDTO expectedResponses = new DisplayResponsesDTO(1L,"SQL",QuizType.TEST,List.of(new QuestionResponseDTO(1L,"Question",QuestionType.SINGLE,List.of(new DisplayOptionDTO(1L,"Option",true)),new DisplayResponseDTO(1L,null,new OptionResponseDTO(1L,List.of(1L))),1)),1,true,false);
        DisplayResponsesDTO result1 = displayResponseService.displayResponsesOfUserInSchedule(1L,1L);
        assertEquals(expectedResponses,result1);

        Question question2 = new Question(2L,"Question", QuestionType.TEXT,null,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz2 = new Quiz(2L,"GK", QuizType.OPEN_ENDED,List.of(question2));
        Scheduler scheduler2 = new Scheduler(2L,quiz2,List.of(user1),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-24 12:30:45.123456789"),Timestamp.valueOf("2024-01-25 12:30:45.123456789"),null,false);
        TextResponse textResponse = new TextResponse(1L,"answer",new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Response response2 = new Response(2L,question2,textResponse,null,user1,scheduler2,-1,new Timestamp(System.currentTimeMillis()),new Timestamp(System.currentTimeMillis()),false,"ChandraMouli",null,true,false);
        when(schedulerService.getScheduleById(2L)).thenReturn(scheduler2);
        when(displayQuestionService.getQuestionById(2L,false)).thenReturn(question2);
        when(responseRepository.findByUserAndSchedulerAndQuestionAndInActive(user1, scheduler2, question2, false)).thenReturn(response2);
        DisplayResponsesDTO expectedResponses1 = new DisplayResponsesDTO(2L,"GK",QuizType.OPEN_ENDED,List.of(new QuestionResponseDTO(2L,"Question",QuestionType.TEXT,new ArrayList<>(),new DisplayResponseDTO(2L,new TextResponseDTO(1L,"answer"),null),-1)),-1,true,false);
        DisplayResponsesDTO result2 = displayResponseService.displayResponsesOfUserInSchedule(1L,2L);
        assertEquals(expectedResponses1,result2);
    }
    @Test
    public void testGetResponse_throwsResourceNotFoundException(){
        Quiz quiz = new Quiz(); quiz.setQuizName("Quiz");
        Scheduler scheduler = new Scheduler(); scheduler.setQuiz(quiz);
        Exception exception = assertThrows(ResourceNotFoundException.class,() -> displayResponseService.getResponse(new User(),scheduler,null));
        assertEquals("Question not found in the quiz " + scheduler.getQuiz().getQuizName(),exception.getMessage());
        Question question = new Question(); question.setQuestionText("Question");quiz.setQuestionList(List.of(question));
        Exception exception1 = assertThrows(ResourceNotFoundException.class,() -> displayResponseService.getResponse(new User(),scheduler,question));
        assertEquals("Response not found for Question " + question.getQuestionText(),exception1.getMessage());

    }

    @Test
    public void testGetResponseById_throwsResourceNotFoundException(){
        Exception exception = assertThrows(ResourceNotFoundException.class,() -> displayResponseService.getResponseById(1L));
        assertEquals("Response not found with Id: " + 1,exception.getMessage());
    }
    @Test
    public void testGetResponseById_throwsInvalidResourceDetailsException(){
        Exception exception = assertThrows(InvalidResourceDetailsException.class,() -> displayResponseService.getResponseById(null));
        assertEquals("Response Id must be provided!",exception.getMessage());
    }
    @Test
    public void testDisplayAllResponsesOfPoll(){
        User user = new User(1L,"Chandra","Mouli","chandramoulikodidasu@gmail.com","Pass@123","8074703740", Role.RESPONDER,false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null);
        Option option = new Option(1L,"Option",true,"Default");
        Question question = new Question(1L,"Question", QuestionType.SINGLE,List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(1L,"GK", QuizType.POLL,List.of(question));
        Scheduler scheduler = new Scheduler(1L,quiz,List.of(user),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-24 12:30:45.123456789"),Timestamp.valueOf("2024-01-25 12:30:45.123456789"),null,false);
        OptionResponse optionResponse = new OptionResponse(1L,List.of(option),new Timestamp(System.currentTimeMillis()),"ChandraMouli",new Timestamp(System.currentTimeMillis()),null,false);
        Response response = new Response(1L,question,null,optionResponse,user,scheduler,-1,new Timestamp(System.currentTimeMillis()),new Timestamp(System.currentTimeMillis()),false,"ChandraMouli",null,true,false);
        when(schedulerService.getScheduleById(1L)).thenReturn(scheduler);
        when(responseRepository.findAllBySchedulerAndInActive(scheduler,false)).thenReturn(List.of(response));
        PollResponsesDTO pollResponsesDTO = new PollResponsesDTO("GK",1L,"Question",List.of(new OptionDTO(1L,"Option",1)),1);
        Object result = displayResponseService.displayAllResponsesOfPoll(1L);
        assertEquals(pollResponsesDTO,result);
    }
}