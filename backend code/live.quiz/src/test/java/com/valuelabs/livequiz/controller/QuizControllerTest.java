package com.valuelabs.livequiz.controller;

import com.valuelabs.livequiz.model.dto.request.OptionCreationDTO;
import com.valuelabs.livequiz.model.dto.request.QuestionCreationDTO;
import com.valuelabs.livequiz.model.dto.request.QuizCreationDTO;
import com.valuelabs.livequiz.model.dto.request.QuizMetaDataDTO;
import com.valuelabs.livequiz.model.dto.response.*;
import com.valuelabs.livequiz.model.entity.Quiz;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.service.question.QuestionService;
import com.valuelabs.livequiz.service.quiz.DisplayQuizService;
import com.valuelabs.livequiz.service.quiz.QuizService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class QuizControllerTests {
    @Mock
    private QuizService quizService;
    @Mock
    private DisplayQuizService displayQuizService;
    @Mock
    private QuestionService questionService;
    @InjectMocks
    private QuizController quizController;
    @Test
    void testCreateQuiz() {
        // Mocking
        QuizCreationDTO quizCreationDTO = new QuizCreationDTO(
                "Java Basics Quiz",
                QuizType.TEST,
                Arrays.asList(1L, 2L, 3L),
                Arrays.asList(
                        new QuestionCreationDTO(
                                QuestionType.SINGLE,
                                "What is Java?",
                                Arrays.asList(
                                        new OptionCreationDTO("A programming language", true),
                                        new OptionCreationDTO("A type of coffee", false)
                                )
                        ),
                        new QuestionCreationDTO(
                                QuestionType.MULTIPLE,
                                "Which of the following are Java IDEs?",
                                Arrays.asList(
                                        new OptionCreationDTO("Eclipse", true),
                                        new OptionCreationDTO("Visual Studio Code", true),
                                        new OptionCreationDTO("Sublime Text", false)
                                )
                        )
                ),null
        );
        Quiz quiz = new Quiz();
        Mockito.when(quizService.createQuiz(quizCreationDTO)).thenReturn(quiz);

        // Test
        ResponseEntity<?> responseEntity = quizController.createQuiz(quizCreationDTO);

        // Assertions
        Mockito.verify(quizService, Mockito.times(1)).createQuiz(quizCreationDTO);
        assert responseEntity.getStatusCode() == HttpStatus.CREATED;
        assert responseEntity.getBody().equals(quiz);
    }

    @Test
    void testGetQuizById() {
        // Mocking
        Long quizId = 1L;
        DisplayQuizDTO displayQuizDTO = new DisplayQuizDTO(
                101L,
                "Java Basics Quiz",
                QuizType.TEST,
                Arrays.asList(
                        new DisplayQuestionDTO(
                                1L,
                                "What is Java?",
                                QuestionType.SINGLE,
                                Arrays.asList(
                                        new DisplayOptionDTO(1L, "A programming language", true),
                                        new DisplayOptionDTO(2L, "A type of coffee", false)
                                )
                        ),
                        new DisplayQuestionDTO(
                                2L,
                                "Which of the following are Java IDEs?",
                                QuestionType.MULTIPLE,
                                Arrays.asList(
                                        new DisplayOptionDTO(3L, "Eclipse", true),
                                        new DisplayOptionDTO(4L, "Visual Studio Code", true),
                                        new DisplayOptionDTO(5L, "Sublime Text", false)
                                )
                        )
                )
        );
        Mockito.when(displayQuizService.displayQuiz(quizId, false)).thenReturn(displayQuizDTO);

        // Test
        ResponseEntity<?> responseEntity = quizController.getQuizById(quizId);

        // Assertions
        Mockito.verify(displayQuizService, Mockito.times(1)).displayQuiz(quizId, false);
        assert responseEntity.getStatusCode() == HttpStatus.FOUND;
        assert responseEntity.getBody().equals(displayQuizDTO);
    }

    @Test
    void testGetAllQuizzes() {
        // Mocking
        List<QuizDisplayDTO> quizzes = new ArrayList<>();
        Mockito.when(displayQuizService.displayAllQuizzes(false)).thenReturn(quizzes);

        // Test
        ResponseEntity<?> responseEntity = quizController.getAllQuizzes();

        // Assertions
        Mockito.verify(displayQuizService, Mockito.times(1)).displayAllQuizzes(false);
        assert responseEntity.getStatusCode() == HttpStatus.FOUND;
        assert responseEntity.getBody().equals(quizzes);
    }

    @Test
    void testAddQuestionsInQuiz() {
        // Mocking
        Long quizId = 1L;
        List<Long> questionIds = new ArrayList<>();
        Mockito.doNothing().when(questionService).addQuestionsInQuiz(quizId, questionIds);

        // Test
        ResponseEntity<?> responseEntity = quizController.addQuestionsInQuiz(quizId, questionIds);

        // Assertions
        Mockito.verify(questionService, Mockito.times(1)).addQuestionsInQuiz(quizId, questionIds);
        assert responseEntity.getStatusCode() == HttpStatus.CREATED;
        assert responseEntity.getBody().equals(new ErrorStatusDTO("Questions added successfully!"));
    }

    @Test
    void testDeleteQuestionInQuiz() {
        // Mocking
        Long quizId = 1L;
        Long questionId = 2L;
        Mockito.doNothing().when(questionService).deleteQuestionInQuiz(quizId, questionId);

        // Test
        ResponseEntity<?> responseEntity = quizController.deleteQuestionInQuiz(quizId, questionId);

        // Assertions
        Mockito.verify(questionService, Mockito.times(1)).deleteQuestionInQuiz(quizId, questionId);
        assert responseEntity.getStatusCode() == HttpStatus.CREATED;
        assert responseEntity.getBody().equals(new ErrorStatusDTO("Question deleted successfully in Quiz!"));
    }

    @Test
    void testUpdateQuiz() {
        // Mocking
        Long quizId = 1L;
        QuizMetaDataDTO quizMetaDataDTO = new QuizMetaDataDTO("Java Basics Quiz");
        Quiz quiz = new Quiz();
        Mockito.when(quizService.updateQuiz(quizId, quizMetaDataDTO)).thenReturn(quiz);

        // Test
        ResponseEntity<?> responseEntity = quizController.updateQuiz(quizId, quizMetaDataDTO);

        // Assertions
        Mockito.verify(quizService, Mockito.times(1)).updateQuiz(quizId, quizMetaDataDTO);
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody().equals(quiz);
    }

    @Test
    void testDeleteQuiz() {
        // Mocking
        Long quizId = 1L;
        Mockito.doNothing().when(quizService).deleteQuizById(quizId);

        // Test
        ResponseEntity<?> responseEntity = quizController.deleteQuiz(quizId);

        // Assertions
        Mockito.verify(quizService, Mockito.times(1)).deleteQuizById(quizId);
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody().equals("Successfully deleted Quiz");
    }
}