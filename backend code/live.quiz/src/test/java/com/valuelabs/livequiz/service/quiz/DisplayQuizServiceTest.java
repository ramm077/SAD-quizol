package com.valuelabs.livequiz.service.quiz;

import static org.junit.jupiter.api.Assertions.*;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.model.dto.response.DisplayQuestionDTO;
import com.valuelabs.livequiz.model.dto.response.DisplayQuizDTO;
import com.valuelabs.livequiz.model.dto.response.QuizDisplayDTO;
import com.valuelabs.livequiz.model.entity.Quiz;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.repository.QuizRepository;
import com.valuelabs.livequiz.service.question.DisplayQuestionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisplayQuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private DisplayQuestionService displayQuestionService;

    @InjectMocks
    private DisplayQuizService displayQuizService;

    @Test
    void testGetQuizById() {
        Long quizId = 1L;
        boolean inActive = false;

        Quiz expectedQuiz = new Quiz();
        when(quizRepository.findByQuizIdAndInActive(quizId, inActive)).thenReturn(java.util.Optional.of(expectedQuiz));

        Quiz result = displayQuizService.getQuizById(quizId, inActive);

        assertEquals(expectedQuiz, result);
    }

    @Test
    void testGetQuizById_InvalidQuizId() {
        Long quizId = null;
        boolean inActive = false;

        InvalidResourceDetailsException exception = assertThrows(InvalidResourceDetailsException.class,
                () -> displayQuizService.getQuizById(quizId, inActive));

        assertEquals("QuizId must be provided!", exception.getMessage());
    }

    @Test
    void testDisplayQuiz() {
        Long quizId = 1L;
        boolean inActive = false;
        Quiz quiz = new Quiz(quizId,"Sample Quiz",QuizType.TEST,new ArrayList<>());
        List<DisplayQuestionDTO> displayQuestions = new ArrayList<>();
        when(displayQuestionService.displayQuestionsInQuiz(anyList(), eq(inActive))).thenReturn(displayQuestions);

        when(quizRepository.findByQuizIdAndInActive(quizId, inActive)).thenReturn(Optional.of(quiz));
        Quiz resultQuiz = displayQuizService.getQuizById(quizId,false);
        assertEquals(quiz,resultQuiz);
        DisplayQuizDTO result = displayQuizService.displayQuiz(quizId, inActive);

        assertEquals(quizId, result.quizId());
        assertEquals("Sample Quiz", result.quizName());
        assertEquals(QuizType.TEST, result.quizType());
    }

    @Test
    void testGetAllQuizzes() {
        boolean inActive = false;

        List<Quiz> expectedQuizzes = new ArrayList<>();
        expectedQuizzes.add(new Quiz());
        when(quizRepository.findAllByInActive(inActive)).thenReturn(expectedQuizzes);

        List<Quiz> result = displayQuizService.getAllQuizzes(inActive);

        assertEquals(expectedQuizzes, result);
    }

    @Test
    void testGetAllQuizzes_FilterByType() {
        QuizType quizType = QuizType.TEST;
        boolean inActive = false;

        List<Quiz> expectedQuizzes = new ArrayList<>();
        expectedQuizzes.add(new Quiz());
        when(quizRepository.findAllByQuizTypeAndInActive(quizType, inActive)).thenReturn(expectedQuizzes);

        List<Quiz> result = displayQuizService.getAllQuizzes(quizType, inActive);

        assertEquals(expectedQuizzes, result);
    }

    @Test
    void testDisplayAllQuizzes() {
        boolean inActive = false;

        List<Quiz> expectedQuizzes = new ArrayList<>();
        expectedQuizzes.add(new Quiz());
        when(quizRepository.findAllByInActive(inActive)).thenReturn(expectedQuizzes);

        List<QuizDisplayDTO> result = displayQuizService.displayAllQuizzes(inActive);

        assertEquals(expectedQuizzes.size(), result.size());
        assertEquals(expectedQuizzes.get(0).getQuizId(), result.get(0).quizId());
        assertEquals(expectedQuizzes.get(0).getQuizName(), result.get(0).quizName());
        assertEquals(expectedQuizzes.get(0).getQuizType(), result.get(0).quizType());
    }

    @Test
    void testDisplayAllTypeQuizzes() {
        QuizType quizType = QuizType.TEST;
        boolean inActive = false;

        List<Quiz> expectedQuizzes = new ArrayList<>();
        expectedQuizzes.add(new Quiz());
        when(quizRepository.findAllByQuizTypeAndInActive(quizType, inActive)).thenReturn(expectedQuizzes);

        List<QuizDisplayDTO> result = displayQuizService.displayAllTypeQuizzes(quizType, inActive);

        assertEquals(expectedQuizzes.size(), result.size());
        assertEquals(expectedQuizzes.get(0).getQuizId(), result.get(0).quizId());
        assertEquals(expectedQuizzes.get(0).getQuizName(), result.get(0).quizName());
        assertEquals(expectedQuizzes.get(0).getQuizType(), result.get(0).quizType());
    }
}