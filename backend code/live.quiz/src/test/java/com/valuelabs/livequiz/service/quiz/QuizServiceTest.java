
package com.valuelabs.livequiz.service.quiz;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceExistsException;
import com.valuelabs.livequiz.model.dto.request.QuizCreationDTO;
import com.valuelabs.livequiz.model.dto.request.QuizMetaDataDTO;
import com.valuelabs.livequiz.model.entity.Option;
import com.valuelabs.livequiz.model.entity.Question;
import com.valuelabs.livequiz.model.entity.Quiz;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.repository.QuizRepository;
import com.valuelabs.livequiz.service.question.QuestionService;
import com.valuelabs.livequiz.service.quiz.DisplayQuizService;
import com.valuelabs.livequiz.service.quiz.QuizService;
import com.valuelabs.livequiz.service.scheduler.UpdateSchedulerService;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionService questionService;

    @Mock
    private DisplayQuizService displayQuizService;

    @Mock
    private UpdateSchedulerService updateSchedulerService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private QuizService quizService;

    @Test
    void testCreateQuiz() {
        Option option =new Option(1L,"Good",true,"Default");
        Question question = new Question(1L,"How are you", QuestionType.SINGLE,List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        QuizCreationDTO quizCreationDTO = new QuizCreationDTO("Sample Quiz", QuizType.TEST,List.of(1L),List.of(),null);
        Quiz quiz = new Quiz(null,"Sample Quiz",QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"",null);
        when(quizRepository.findByQuizNameAndInActive("Sample Quiz", false)).thenReturn(null);
        when(questionService.addQuestions(List.of(1L))).thenReturn(new ArrayList<>());
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);
        Quiz result = quizService.createQuiz(quizCreationDTO);

        assertNotNull(result);
        assertEquals("Sample Quiz", result.getQuizName());
        assertEquals(QuizType.TEST, result.getQuizType());
    }

    @Test
    void testCreateQuiz_ExistingActiveQuiz() {
        QuizCreationDTO quizCreationDTO = new QuizCreationDTO("Sample Quiz", QuizType.TEST,List.of(1L),List.of(),null);
        when(quizRepository.findByQuizNameAndInActive("Sample Quiz", false)).thenReturn(new Quiz());

        ResourceExistsException exception = assertThrows(ResourceExistsException.class,
                () -> quizService.createQuiz(quizCreationDTO));

        assertEquals("Active Quiz with quiz name Sample Quiz is already Present", exception.getMessage());
    }

    @Test
    void testUpdateQuiz() {
        Long quizId = 1L;
        QuizMetaDataDTO quizMetaDataDTO = new QuizMetaDataDTO("Updated Quiz");
        Option option =new Option(1L,"Good",true,"Default");
        Question question = new Question(1L,"How are you", QuestionType.SINGLE,List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(1L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);


        when(displayQuizService.getQuizById(quizId, false)).thenReturn(quiz);
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);
        Quiz result = quizService.updateQuiz(quizId, quizMetaDataDTO);

        assertNotNull(result);
        assertEquals("Updated Quiz", result.getQuizName());
    }

    @Test
    void testUpdateQuiz_InvalidQuizId() {
        Long quizId = null;
        QuizMetaDataDTO quizMetaDataDTO = new QuizMetaDataDTO(("Updated Quiz"));


        InvalidResourceDetailsException exception = assertThrows(InvalidResourceDetailsException.class,
                () -> quizService.updateQuiz(quizId, quizMetaDataDTO));

        assertEquals("Quiz Id must be provided!", exception.getMessage());
    }

    @Test
    void testDeleteQuizById() {
        Long quizId = 1L;
        Quiz quiz = new Quiz();
        quiz.setQuizId(quizId);

        when(displayQuizService.getQuizById(quizId, false)).thenReturn(quiz);
        when(updateSchedulerService.findScheduleByQuiz(quiz)).thenReturn(false);

        quizService.deleteQuizById(quizId);

        assertTrue(quiz.getInActive());
    }

    @Test
    void testDeleteQuizById_ScheduledQuiz() {
        Long quizId = 1L;
        Quiz quiz = new Quiz();
        quiz.setQuizId(quizId);

        when(displayQuizService.getQuizById(quizId, false)).thenReturn(quiz);
        when(updateSchedulerService.findScheduleByQuiz(quiz)).thenReturn(true);

        ResourceExistsException exception = assertThrows(ResourceExistsException.class,
                () -> quizService.deleteQuizById(quizId));

        assertEquals("This Quiz is already Scheduled", exception.getMessage());
    }
}

