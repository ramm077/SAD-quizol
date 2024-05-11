package com.valuelabs.livequiz.service.response;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceExistsException;
import com.valuelabs.livequiz.model.dto.request.CaptureResponseDTO;
import com.valuelabs.livequiz.model.dto.request.CaptureResponsesDTO;
import com.valuelabs.livequiz.model.entity.*;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.model.enums.Role;
import com.valuelabs.livequiz.repository.ResponseRepository;
import com.valuelabs.livequiz.service.option.DisplayOptionService;
import com.valuelabs.livequiz.service.optionresponse.OptionResponseService;
import com.valuelabs.livequiz.service.question.DisplayQuestionService;
import com.valuelabs.livequiz.service.scheduler.CreateSchedulerService;
import com.valuelabs.livequiz.service.textresponse.TextResponseService;
import com.valuelabs.livequiz.service.user.DisplayUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResponseCaptureServiceTest {

    @Mock
    private DisplayUserService displayUserService;
    @Mock
    private DisplayQuestionService displayQuestionService;
    @Mock
    private DisplayOptionService displayOptionService;
    @Mock
    private ResponseRepository responseRepository;

    @Mock
    private CreateSchedulerService schedulerService;

    @Mock
    private OptionResponseService optionResponseService;

    @Mock
    private TextResponseService textResponseService;

    @InjectMocks
    private ResponseCaptureService responseCaptureService;

    @Test
    public void testCaptureResponseService_throwsInvalidResourceDetailsException(){
//        Long userId = null;
        when(displayUserService.getUserByIdAndInActive(null,false)).thenThrow(InvalidResourceDetailsException.class);
        CaptureResponsesDTO responsesDTO10 = new CaptureResponsesDTO(null,false);
        assertThrows(InvalidResourceDetailsException.class, () -> responseCaptureService.captureResponsesOfPersonInQuiz(null,null,responsesDTO10));

        User user = new User(1L,"Chandra","Mouli","chandramoulikodidasu@gmail.com","Pass@123","8074703740", Role.RESPONDER,false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null);

//        Long userId = 1L;

        when(displayUserService.getUserByIdAndInActive(1L,false)).thenReturn(user);
        when(schedulerService.getScheduleById(null)).thenThrow(InvalidResourceDetailsException.class);
        assertThrows(InvalidResourceDetailsException.class, () -> responseCaptureService.captureResponsesOfPersonInQuiz(1L,null,null));


        Quiz quiz = new Quiz(1L,"SQL", QuizType.OPEN_ENDED,new ArrayList<>());

        Scheduler scheduler = new Scheduler(1L,quiz,List.of(user),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-24 12:30:45.123456789"),Timestamp.valueOf("2024-01-25 12:30:45.123456789"),null,false);
//        Long schedulerId = 1L;
        when(schedulerService.getScheduleById(1L)).thenReturn(scheduler);

        CaptureResponseDTO responseDTO  = new CaptureResponseDTO(1L, QuestionType.TEXT,null,"answer");
        CaptureResponsesDTO responsesDTO = new CaptureResponsesDTO(List.of(responseDTO),false);
        when(displayQuestionService.getQuestionById(1L,false)).thenThrow(InvalidResourceDetailsException.class);

        assertThrows(InvalidResourceDetailsException.class, () -> responseCaptureService.captureResponsesOfPersonInQuiz(1L,1L,responsesDTO));


        //questionId == null
        CaptureResponseDTO responseDTO1 = new CaptureResponseDTO(null,null,null,null);
        CaptureResponsesDTO responsesDTO1 = new CaptureResponsesDTO(List.of(responseDTO1),false);

        assertThrows(InvalidResourceDetailsException.class, () -> responseCaptureService.captureResponsesOfPersonInQuiz(1L,1L,responsesDTO1));


        //questionType == null

        CaptureResponseDTO responseDTO2 = new CaptureResponseDTO(1L,null,null,null);
        CaptureResponsesDTO responsesDTO2 = new CaptureResponsesDTO(List.of(responseDTO2),false);

        assertThrows(InvalidResourceDetailsException.class, () -> responseCaptureService.captureResponsesOfPersonInQuiz(1L,1L,responsesDTO2));


        Question question = new Question(2L,"Question", QuestionType.TEXT,null,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);

        when(displayQuestionService.getQuestionById(2L,false)).thenReturn(question);

        quiz.getQuestionList().add(question);

        CaptureResponseDTO responseDTO3 = new CaptureResponseDTO(2L,QuestionType.TEXT,null,null);
        CaptureResponsesDTO responsesDTO3 = new CaptureResponsesDTO(List.of(responseDTO3),false);

        when(responseRepository.findByUserAndSchedulerAndQuestionAndInActive(user, scheduler, question, false)).thenReturn(null);

        when(textResponseService.createTextResponse(responseDTO3.answerText())).thenThrow(InvalidResourceDetailsException.class);

        assertThrows(InvalidResourceDetailsException.class, () -> responseCaptureService.captureResponsesOfPersonInQuiz(1L,1L,responsesDTO3));


        Question question1 = new Question(3L,"Question", QuestionType.SINGLE,new ArrayList<>(),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz1 = new Quiz(2L,"Quiz 1",QuizType.OPEN_ENDED,List.of(question1));
        Scheduler scheduler1 = new Scheduler(2L,quiz1,List.of(user),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-24 12:30:45.123456789"),Timestamp.valueOf("2024-01-25 12:30:45.123456789"),null,false);
        when(displayQuestionService.getQuestionById(3L,false)).thenReturn(question1);
        when(schedulerService.getScheduleById(2L)).thenReturn(scheduler1);

        CaptureResponseDTO responseDTO4 = new CaptureResponseDTO(3L,QuestionType.SINGLE,new ArrayList<>(),null);
        CaptureResponsesDTO responsesDTO4 = new CaptureResponsesDTO(List.of(responseDTO4),false);

        List<Option> chosenOptions = new ArrayList<>();

        when(optionResponseService.createOptionResponse(chosenOptions)).thenThrow(InvalidResourceDetailsException.class);

        assertThrows(InvalidResourceDetailsException.class, () -> responseCaptureService.captureResponsesOfPersonInQuiz(1L,2L,responsesDTO4));


        Quiz quiz2 = new Quiz(3L,"Quiz 2",QuizType.TEST,List.of(question1));
        Scheduler scheduler2 = new Scheduler(3L,quiz2,List.of(user),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-24 12:30:45.123456789"),Timestamp.valueOf("2024-01-25 12:30:45.123456789"),null,false);

        when(schedulerService.getScheduleById(3L)).thenReturn(scheduler2);

        assertThrows(InvalidResourceDetailsException.class, () -> responseCaptureService.captureResponsesOfPersonInQuiz(1L,3L,responsesDTO4));


        responseDTO4.chosenOptions().add(null);

        when(displayOptionService.getOptionById(null,false)).thenThrow(InvalidResourceDetailsException.class);

        assertThrows(InvalidResourceDetailsException.class, () -> responseCaptureService.captureResponsesOfPersonInQuiz(1L,3L,responsesDTO4));

    }

    @Test
    public void testCaptureResponseService_throwsResourceExistsException(){
        User user = new User(1L,"Chandra","Mouli","chandramoulikodidasu@gmail.com","Pass@123","8074703740", Role.RESPONDER,false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null);
        Question question = new Question(2L,"Question", QuestionType.TEXT,null,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(1L,"SQL", QuizType.OPEN_ENDED,List.of(question));
        Scheduler scheduler = new Scheduler(1L,quiz,List.of(user),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-24 12:30:45.123456789"),Timestamp.valueOf("2024-01-25 12:30:45.123456789"),null,false);
        when(displayQuestionService.getQuestionById(2L,false)).thenReturn(question);
        when(displayUserService.getUserByIdAndInActive(1L,false)).thenReturn(user);
        when(schedulerService.getScheduleById(1L)).thenReturn(scheduler);
        Response response = new Response(user,scheduler,question);
        when(responseRepository.findByUserAndSchedulerAndQuestionAndInActive(user, scheduler, question, false)).thenReturn(response);
        CaptureResponseDTO responseDTO  = new CaptureResponseDTO(2L, QuestionType.TEXT,null,"answer");
        CaptureResponsesDTO responsesDTO = new CaptureResponsesDTO(List.of(responseDTO),false);

        Exception exception = assertThrows(ResourceExistsException.class,() -> responseCaptureService.captureResponsesOfPersonInQuiz(1L,1L,responsesDTO));
        assertEquals("The User with UserId: 1 has already given response for question with questionId: 2 in Schedule with scheduleId: 1. This user can only update his responses for this quiz",exception.getMessage());
    }

    @Test
    public void testCaptureResponsesOfPersonInQuiz_Success(){
        User user1 = new User(1L,"Chandra","Mouli","chandramoulikodidasu@gmail.com","Pass@123","8074703740", Role.RESPONDER,false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null);
        Option option1 = new Option(1L,"Option",true,"Default");
        Option option3 = new Option(3L,"Option",true,"Default");
        Option option4 = new Option(4L,"Option",true,"Default");
        Question question1 = new Question(1L,"Question", QuestionType.SINGLE,List.of(option1),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Question question3 = new Question(3L,"Question", QuestionType.SINGLE,List.of(option3,option4),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz1 = new Quiz(1L,"SQL", QuizType.TEST,List.of(question1,question3));
        Scheduler scheduler1 = new Scheduler(1L,quiz1,List.of(user1),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-24 12:30:45.123456789"),Timestamp.valueOf("2024-01-25 12:30:45.123456789"),null,false);
        when(displayOptionService.getOptionById(1L,false)).thenReturn(option1);
        when(displayQuestionService.getQuestionById(1L,false)).thenReturn(question1);
        when(displayOptionService.getOptionById(3L,false)).thenReturn(option3);
        when(displayQuestionService.getQuestionById(3L,false)).thenReturn(question3);
        when(displayUserService.getUserByIdAndInActive(1L,false)).thenReturn(user1);
        when(schedulerService.getScheduleById(1L)).thenReturn(scheduler1);
        when(responseRepository.findByUserAndSchedulerAndQuestionAndInActive(user1, scheduler1, question1, false)).thenReturn(null);
        when(responseRepository.findByUserAndSchedulerAndQuestionAndInActive(user1, scheduler1, question3, false)).thenReturn(null);
        CaptureResponseDTO responseDTO1 = new CaptureResponseDTO(1L,QuestionType.SINGLE,List.of(1L),null);
        CaptureResponseDTO responseDTO3 = new CaptureResponseDTO(3L,QuestionType.SINGLE,List.of(3L),null);
        CaptureResponsesDTO responsesDTO1 = new CaptureResponsesDTO(List.of(responseDTO1,responseDTO3),false);
        List<Option> chosenOptions1 = List.of(option1);
        List<Option> chosenOptions3 = List.of(option3);
        OptionResponse optionResponse1 = new OptionResponse(1L,List.of(option1),new Timestamp(System.currentTimeMillis()),"ChandraMouli",new Timestamp(System.currentTimeMillis()),null,false);
        when(optionResponseService.createOptionResponse(chosenOptions1)).thenReturn(optionResponse1);
        OptionResponse optionResponse3 = new OptionResponse(3L,List.of(option3),new Timestamp(System.currentTimeMillis()),"ChandraMouli",new Timestamp(System.currentTimeMillis()),null,false);
        when(optionResponseService.createOptionResponse(chosenOptions3)).thenReturn(optionResponse3);
        Response expectedResponse1 = new Response(null,question1,null,optionResponse1,user1,scheduler1,1,new Timestamp(System.currentTimeMillis()),new Timestamp(System.currentTimeMillis()),false,"ChandraMouli",null,true,false);
        Response expectedResponse3 = new Response(null,question3,null,optionResponse1,user1,scheduler1,0,new Timestamp(System.currentTimeMillis()),new Timestamp(System.currentTimeMillis()),false,"ChandraMouli",null,true,false);

        List<Response> result1 = responseCaptureService.captureResponsesOfPersonInQuiz(1L,1L,responsesDTO1);
        assertEquals(expectedResponse1.getScoreCount(),result1.get(0).getScoreCount());
        assertEquals(expectedResponse3.getScoreCount(),result1.get(1).getScoreCount());

        User user2 = new User(2L,"Chandra","Mouli","chandramoulikodidasu@gmail.com","Pass@123","8074703740", Role.RESPONDER,false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null);
        Option option2 = new Option(2L,"Option",true,"Default");
        Question question2 = new Question(2L,"Question", QuestionType.SINGLE,List.of(option2),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Question question4 = new Question(4L,"Question", QuestionType.TEXT,null,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz2 = new Quiz(2L,"SQL1", QuizType.OPEN_ENDED,List.of(question2,question4));
        Scheduler scheduler2 = new Scheduler(2L,quiz2,List.of(user2),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-24 12:30:45.123456789"),Timestamp.valueOf("2024-01-25 12:30:45.123456789"),null,false);
        when(displayOptionService.getOptionById(2L,false)).thenReturn(option2);
        when(displayQuestionService.getQuestionById(2L,false)).thenReturn(question2);
        when(displayQuestionService.getQuestionById(4L,false)).thenReturn(question4);
        when(displayUserService.getUserByIdAndInActive(2L,false)).thenReturn(user2);
        when(schedulerService.getScheduleById(2L)).thenReturn(scheduler2);
        when(responseRepository.findByUserAndSchedulerAndQuestionAndInActive(user2, scheduler2, question2, false)).thenReturn(null);
        when(responseRepository.findByUserAndSchedulerAndQuestionAndInActive(user2, scheduler2, question4, false)).thenReturn(null);
        CaptureResponseDTO responseDTO2 = new CaptureResponseDTO(2L,QuestionType.SINGLE,List.of(2L),null);
        CaptureResponseDTO responseDTO4 = new CaptureResponseDTO(4L,QuestionType.TEXT,null,"answer");
        CaptureResponsesDTO responsesDTO2 = new CaptureResponsesDTO(List.of(responseDTO2,responseDTO4),false);
        List<Option> chosenOptions2 = List.of(option2);
        OptionResponse optionResponse2 = new OptionResponse(1L,List.of(option2),new Timestamp(System.currentTimeMillis()),"ChandraMouli",new Timestamp(System.currentTimeMillis()),null,false);
        when(optionResponseService.createOptionResponse(chosenOptions2)).thenReturn(optionResponse2);
        TextResponse textResponse1 = new TextResponse(1L,"answer",new Timestamp(System.currentTimeMillis()),"ChandraMouli",new Timestamp(System.currentTimeMillis()),null,false);
        when(textResponseService.createTextResponse("answer")).thenReturn(textResponse1);
        Response expectedResponse2 = new Response(null,question2,null,optionResponse2,user2,scheduler2,-1,new Timestamp(System.currentTimeMillis()),new Timestamp(System.currentTimeMillis()),false,"ChandraMouli",null,true,false);
        Response expectedResponse4 = new Response(null,question4,textResponse1,null,user2,scheduler2,-1,new Timestamp(System.currentTimeMillis()),new Timestamp(System.currentTimeMillis()),false,"ChandraMouli",null,true,false);
        List<Response> result2 = responseCaptureService.captureResponsesOfPersonInQuiz(2L,2L,responsesDTO2);
        assertEquals(expectedResponse2.getScoreCount(),result2.get(0).getScoreCount());
        assertEquals(expectedResponse4.getScoreCount(),result2.get(1).getScoreCount());

    }

    @Test
    public void saveResponse_throwInvalidResourceDetailsException(){
        Response response = null;
        Exception exception = assertThrows(InvalidResourceDetailsException.class, () -> responseCaptureService.saveResponse(response));
        assertEquals("Response must be provided!",exception.getMessage());
    }
    @Test
    public void saveResponse_Success(){
        Response response = new Response();
        when(responseRepository.save(response)).thenReturn(response);
        Response result = responseCaptureService.saveResponse(response);
        assertEquals(result,response);
    }


}

