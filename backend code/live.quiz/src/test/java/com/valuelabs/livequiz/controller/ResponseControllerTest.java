package com.valuelabs.livequiz.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.valuelabs.livequiz.model.dto.request.CaptureResponseDTO;
import com.valuelabs.livequiz.model.dto.request.CaptureResponsesDTO;
import com.valuelabs.livequiz.model.dto.request.UpdateResponsesDTO;
import com.valuelabs.livequiz.model.dto.request.UpdateTextResponseDTO;
import com.valuelabs.livequiz.model.dto.response.*;
import com.valuelabs.livequiz.model.entity.Response;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.service.response.DeleteResponseService;
import com.valuelabs.livequiz.service.response.DisplayResponseService;
import com.valuelabs.livequiz.service.response.ResponseCaptureService;
import com.valuelabs.livequiz.service.response.UpdateResponseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ResponseControllerTests {
    @Mock
    private ResponseCaptureService responseCaptureService;
    @Mock
    private DisplayResponseService displayResponseService;
    @Mock
    private UpdateResponseService updateResponseService;
    @Mock
    private DeleteResponseService deleteResponseService;
    @InjectMocks
    private ResponseController responseController;
    @Test
    void testResponseCaptureOfUserInQuiz() {
        // Mocking
        Long userId = 1L;
        Long schedulerId = 2L;
        CaptureResponseDTO captureResponseDTO =new CaptureResponseDTO(1L,QuestionType.SINGLE,List.of(1L),"");
        CaptureResponsesDTO responseDTOS = new CaptureResponsesDTO(List.of(captureResponseDTO),false);
        List<Response> responseList = new ArrayList<>();
        Mockito.when(responseCaptureService.captureResponsesOfPersonInQuiz(userId, schedulerId, responseDTOS)).thenReturn(responseList);

        // Test
        ResponseEntity<?> responseEntity = responseController.responseCaptureOfUserInQuiz(userId, schedulerId, responseDTOS);

        // Assertions
        Mockito.verify(responseCaptureService, Mockito.times(1)).captureResponsesOfPersonInQuiz(userId, schedulerId, responseDTOS);
        assert responseEntity.getStatusCode() == HttpStatus.CREATED;
        assert responseEntity.getBody().equals(responseList);
    }

    @Test
    void testDisplayResponsesOfUserInQuiz() {
        // Mocking
        //Long schedulerId, String quizName, QuizType quizType,
        //                                  List<QuestionResponseDTO> questionList, Integer quizScore,Boolean isAttempted, Boolean finalSubmit)
        Long userId = 1L;
        Long schedulerId = 2L;
        DisplayOptionDTO displayOptionDTO = new DisplayOptionDTO(1L,"Option1",true);
        OptionResponseDTO optionResponseDTO = new OptionResponseDTO(1L,List.of(1L));
        DisplayResponseDTO displayResponseDTO = new DisplayResponseDTO(1L,new TextResponseDTO(1L,"Option1"),optionResponseDTO);
        QuestionResponseDTO questionResponseDTO = new QuestionResponseDTO(1L,"Test question", QuestionType.SINGLE,List.of(displayOptionDTO),displayResponseDTO,0);
        DisplayResponsesDTO responses = new DisplayResponsesDTO(1L,"Test", QuizType.TEST,List.of(questionResponseDTO),0,true,true);
        Mockito.when(displayResponseService.displayResponsesOfUserInSchedule(userId, schedulerId)).thenReturn(responses);

        // Test
        ResponseEntity<?> responseEntity = responseController.displayResponsesOfUserInQuiz(userId, schedulerId);

        // Assertions
        Mockito.verify(displayResponseService, Mockito.times(1)).displayResponsesOfUserInSchedule(userId, schedulerId);
        assert responseEntity.getStatusCode() == HttpStatus.FOUND;
        assert responseEntity.getBody().equals(responses);
    }

    @Test
    void testUpdateResponsesOfUserInQuiz() {
        // Mocking
        Long userId = 1L;
        Long schedulerId = 2L;
        UpdateTextResponseDTO updateTextResponseDTO = new UpdateTextResponseDTO(1L,1L,"Option2");
        UpdateResponsesDTO responseDTO = new UpdateResponsesDTO(List.of(updateTextResponseDTO),List.of(),true);
        List<Response> responseList = new ArrayList<>();
        Mockito.when(updateResponseService.updateResponsesOfPersonInQuiz(userId, schedulerId, responseDTO)).thenReturn(responseList);

        // Test
        ResponseEntity<?> responseEntity = responseController.updateResponsesOfUserInQuiz(userId, schedulerId, responseDTO);

        // Assertions
        Mockito.verify(updateResponseService, Mockito.times(1)).updateResponsesOfPersonInQuiz(userId, schedulerId, responseDTO);
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody().equals(responseList);
    }

    @Test
    void testDeleteResponsesOfUserInQuiz() {
        // Mocking
        Long userId = 1L;
        Long schedulerId = 2L;

        // Test
        ResponseEntity<?> responseEntity = responseController.deleteResponsesOfUserInQuiz(userId, schedulerId);

        // Assertions
        Mockito.verify(deleteResponseService, Mockito.times(1)).deleteResponsesOfUserInQuiz(userId, schedulerId);
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody().equals("Responses Deleted Successfully");
    }
}