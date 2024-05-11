package com.valuelabs.livequiz.controller;

import com.valuelabs.livequiz.model.dto.request.OptionCreationDTO;
import com.valuelabs.livequiz.model.dto.request.QuestionCreationDTO;
import com.valuelabs.livequiz.model.dto.response.DisplayOptionDTO;
import com.valuelabs.livequiz.model.dto.response.DisplayQuestionDTO;
import com.valuelabs.livequiz.model.dto.response.ErrorStatusDTO;
import com.valuelabs.livequiz.model.entity.Option;
import com.valuelabs.livequiz.model.entity.Question;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.service.question.DisplayQuestionService;
import com.valuelabs.livequiz.service.question.QuestionService;
import com.valuelabs.livequiz.service.question.UpdateQuestionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class QuestionControllerTests {
    @Mock
    private QuestionService questionService;
    @Mock
    private DisplayQuestionService displayQuestionService;
    @Mock
    private UpdateQuestionService updateQuestionService;
    @InjectMocks
    private QuestionController questionController;
    @Test
    void testCreateQuestion() {
        // Mocking
        OptionCreationDTO optionCreationDTO = new OptionCreationDTO("Option1",false);
        Option option = new Option("Option1",false,"Default");
        QuestionCreationDTO questionCreationDTO = new QuestionCreationDTO(QuestionType.SINGLE,"What is your name?", List.of(optionCreationDTO));
        Question question = new Question(1L, "What is your name?", QuestionType.SINGLE, List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"",false);
        Mockito.when(questionService.createQuestion(questionCreationDTO)).thenReturn(question);

        // Test
        ResponseEntity<?> responseEntity = questionController.createQuestion(questionCreationDTO);

        // Assertions
        Mockito.verify(questionService, Mockito.times(1)).createQuestion(questionCreationDTO);
        assert responseEntity.getStatusCode() == HttpStatus.CREATED;
        assert responseEntity.getBody().equals(question);
    }

    @Test
    void testCreateQuestionInQuiz() {
        // Mocking
        Long quizId = 1L;
        OptionCreationDTO optionCreationDTO = new OptionCreationDTO("Option1",false);
        Option option = new Option("Option1",false,"Default");
        QuestionCreationDTO questionCreationDTO = new QuestionCreationDTO(QuestionType.SINGLE,"What is your name?", List.of(optionCreationDTO));

        // Test
        ResponseEntity<?> responseEntity = questionController.createQuestionInQuiz(quizId, questionCreationDTO);

        // Assertions
        Mockito.verify(questionService, Mockito.times(1)).createQuestionInQuiz(quizId, questionCreationDTO);
        assert responseEntity.getStatusCode() == HttpStatus.CREATED;
        assert responseEntity.getBody().equals(new ErrorStatusDTO("Question created Successfully in Quiz with " + quizId));
    }

    @Test
    void testGetQuestionById() {
        // Mocking
        Long questionId = 1L;
        DisplayQuestionDTO displayQuestionDTO = new DisplayQuestionDTO(1L,"Test",QuestionType.SINGLE,List.of(new DisplayOptionDTO(1L,"Option1",true)));
        Mockito.when(displayQuestionService.displayQuestion(questionId, false)).thenReturn(displayQuestionDTO);

        // Test
        ResponseEntity<?> responseEntity = questionController.getQuestionById(questionId);

        // Assertions
        Mockito.verify(displayQuestionService, Mockito.times(1)).displayQuestion(questionId, false);
        assert responseEntity.getStatusCode() == HttpStatus.FOUND;
        assert responseEntity.getBody().equals(displayQuestionDTO);
    }

    @Test
    void testGetAllQuestions() {
        // Mocking
        List<DisplayQuestionDTO> displayQuestionDTOList = new ArrayList<>();
        Mockito.when(displayQuestionService.displayAllQuestions(false)).thenReturn(displayQuestionDTOList);

        // Test
        ResponseEntity<?> responseEntity = questionController.getAllQuestions(false);

        // Assertions
        Mockito.verify(displayQuestionService, Mockito.times(1)).displayAllQuestions(false);
        assert responseEntity.getStatusCode() == HttpStatus.FOUND;
        assert responseEntity.getBody().equals(displayQuestionDTOList);
    }

    @Test
    void testDeleteQuestion() {
        // Mocking
        Long questionId = 1L;

        // Test
        ResponseEntity<?> responseEntity = questionController.deleteQuestion(questionId);

        // Assertions
        Mockito.verify(updateQuestionService, Mockito.times(1)).deleteQuestion(questionId);
        assert responseEntity.getStatusCode() == HttpStatus.FOUND;
        assert responseEntity.getBody().equals(new ErrorStatusDTO("Question deleted successfully!"));
    }
}