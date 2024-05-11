package com.valuelabs.livequiz.service.response;

import com.valuelabs.livequiz.model.dto.request.UpdateOptionResponseDTO;
import com.valuelabs.livequiz.model.dto.request.UpdateResponsesDTO;
import com.valuelabs.livequiz.model.dto.request.UpdateTextResponseDTO;
import com.valuelabs.livequiz.model.entity.*;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.model.enums.Role;
import com.valuelabs.livequiz.service.optionresponse.OptionResponseService;
import com.valuelabs.livequiz.service.scheduler.CreateSchedulerService;
import com.valuelabs.livequiz.service.textresponse.TextResponseService;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateResponseServiceTest {
    @Mock
    private CreateSchedulerService schedulerService;

    @Mock
    private TextResponseService textResponseService;

    @Mock
    private OptionResponseService optionResponseService;

    @Mock
    private DisplayResponseService displayResponseService;

    @Mock
    private ResponseCaptureService responseCaptureService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private UpdateResponseService updateResponseService;

    @Test
    public void updateResponsesOfPersonInQuiz_Success(){
        User user1 = new User(1L,"Chandra","Mouli","chandramoulikodidasu@gmail.com","Pass@123","8074703740", Role.RESPONDER,false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null);
        Option option1 = new Option(1L,"Option",true,"Default");
        Option option2 = new Option(2L,"Option",true,"Default");
        Option option3 = new Option(3L,"Option",true,"Default");
        Option option4 = new Option(4L,"Option",true,"Default");
        Question question1 = new Question(1L,"Question", QuestionType.SINGLE,List.of(option1,option2),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Question question2 = new Question(2L,"Question", QuestionType.SINGLE,List.of(option3,option4),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz1 = new Quiz(1L,"SQL", QuizType.TEST,List.of(question1,question2));
        Scheduler scheduler1 = new Scheduler(1L,quiz1,List.of(user1),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-24 12:30:45.123456789"),Timestamp.valueOf("2024-01-25 12:30:45.123456789"),null,false);
        List<Option> chosenOptions1 = List.of(option1,option2);
        List<Option> chosenOptions2 = List.of(option3,option4);
        List<Option> correctOptions1 = List.of(option1,option2);
        List<Option> correctOptions2 = List.of(option3,option4);
        OptionResponse optionResponse1 = new OptionResponse(1L,List.of(option1),new Timestamp(System.currentTimeMillis()),"ChandraMouli",new Timestamp(System.currentTimeMillis()),null,false);
        OptionResponse optionResponse2 = new OptionResponse(2L,List.of(option3),new Timestamp(System.currentTimeMillis()),"ChandraMouli",new Timestamp(System.currentTimeMillis()),null,false);

        Response response1 = new Response(1L,question1,null,optionResponse1,user1,scheduler1,0,new Timestamp(System.currentTimeMillis()),new Timestamp(System.currentTimeMillis()),false,"ChandraMouli",null,true,false);
        Response response2 = new Response(2L,question2,null,optionResponse2,user1,scheduler1,0,new Timestamp(System.currentTimeMillis()),new Timestamp(System.currentTimeMillis()),false,"ChandraMouli",null,true,false);

        UpdateOptionResponseDTO optionDTO1 = new UpdateOptionResponseDTO(1L,1L,1L,List.of(1L,2L));
        UpdateOptionResponseDTO optionDTO2 = new UpdateOptionResponseDTO(2L,2L,2L,List.of(3L,4L));
        UpdateResponsesDTO responsesDTO = new UpdateResponsesDTO(null,List.of(optionDTO1,optionDTO2),false);

        when(schedulerService.getScheduleById(1L)).thenReturn(scheduler1);
        when(optionResponseService.getOptionResponseById(1L)).thenReturn(optionResponse1);
        when(optionResponseService.getOptionResponseById(2L)).thenReturn(optionResponse2);
        when(responseCaptureService.chosenOptions(optionDTO1.questionId(),optionDTO1.chosenOptions())).thenReturn(chosenOptions1);
        when(responseCaptureService.chosenOptions(optionDTO2.questionId(),optionDTO2.chosenOptions())).thenReturn(chosenOptions2);
        when(optionResponseService.saveOptionResponse(optionResponse1)).thenReturn(optionResponse1);
        when(optionResponseService.saveOptionResponse(optionResponse2)).thenReturn(optionResponse2);
        when(displayResponseService.getResponseById(optionDTO1.responseId())).thenReturn(response1);
        when(displayResponseService.getResponseById(optionDTO2.responseId())).thenReturn(response2);
        when(authenticationService.getCurrentUserName()).thenReturn("ChandraMouli");
        when(responseCaptureService.correctOptions(optionDTO1.questionId())).thenReturn(correctOptions1);
        when(responseCaptureService.correctOptions(optionDTO2.questionId())).thenReturn(correctOptions2);
        response1.setScoreCount(1);
        response2.setScoreCount(1);
        when(responseCaptureService.validateOptionsAndSetScore(optionDTO1.questionId(),response1,chosenOptions1,correctOptions1)).thenReturn(response1);
        when(responseCaptureService.validateOptionsAndSetScore(optionDTO2.questionId(),response2,chosenOptions2,correctOptions2)).thenReturn(response2);
        when(responseCaptureService.saveResponse(response1)).thenReturn(response1);
        when(responseCaptureService.saveResponse(response2)).thenReturn(response2);

        List<Response> result = updateResponseService.updateResponsesOfPersonInQuiz(1L,1L,responsesDTO);

        assertEquals(List.of(option1,option2),result.get(0).getOptionResponse().getChosenOptions());
        assertEquals(List.of(option3,option4),result.get(1).getOptionResponse().getChosenOptions());


        Question question3 = new Question(3L,"Question", QuestionType.TEXT,null,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz2 = new Quiz(2L,"GK", QuizType.OPEN_ENDED,List.of(question1,question3));
        Scheduler scheduler2 = new Scheduler(2L,quiz2,List.of(user1),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-24 12:30:45.123456789"),Timestamp.valueOf("2024-01-25 12:30:45.123456789"),null,false);

        OptionResponse optionResponse3 = new OptionResponse(3L,List.of(option1),new Timestamp(System.currentTimeMillis()),"ChandraMouli",new Timestamp(System.currentTimeMillis()),null,false);
        TextResponse textResponse = new TextResponse(1L,"answer",new Timestamp(System.currentTimeMillis()),"ChandraMouli",new Timestamp(System.currentTimeMillis()),null,false);

        Response response3 = new Response(3L,question1,null,optionResponse3,user1,scheduler2,-1,new Timestamp(System.currentTimeMillis()),new Timestamp(System.currentTimeMillis()),false,"ChandraMouli",null,true,false);
        Response response4 = new Response(4L,question3,textResponse,null,user1,scheduler2,-1,new Timestamp(System.currentTimeMillis()),new Timestamp(System.currentTimeMillis()),false,"ChandraMouli",null,true,false);

        UpdateOptionResponseDTO optionDTO3 = new UpdateOptionResponseDTO(1L,3L,3L,List.of(1L,2L));
        UpdateTextResponseDTO textResponseDTO = new UpdateTextResponseDTO(4L,1L,"updatedAnswer");
        UpdateResponsesDTO responsesDTO1 = new UpdateResponsesDTO(List.of(textResponseDTO),List.of(optionDTO3),false);

        when(schedulerService.getScheduleById(2L)).thenReturn(scheduler2);
        when(optionResponseService.getOptionResponseById(3L)).thenReturn(optionResponse3);
        when(responseCaptureService.chosenOptions(optionDTO3.questionId(),optionDTO3.chosenOptions())).thenReturn(chosenOptions1);
        when(optionResponseService.saveOptionResponse(optionResponse3)).thenReturn(optionResponse3);
        when(displayResponseService.getResponseById(optionDTO3.responseId())).thenReturn(response3);
        when(displayResponseService.getResponseById(textResponseDTO.responseId())).thenReturn(response4);
        when(responseCaptureService.saveResponse(response3)).thenReturn(response3);
        when(responseCaptureService.saveResponse(response4)).thenReturn(response4);
        when(textResponseService.getTextResponseById(1L)).thenReturn(textResponse);

        List<Response> result1 = updateResponseService.updateResponsesOfPersonInQuiz(1L,2L,responsesDTO1);

        assertEquals(textResponseDTO.answerText(),result1.get(0).getTextResponse().getAnswerText());
        assertEquals(List.of(option1,option2),result1.get(1).getOptionResponse().getChosenOptions());
    }
}
