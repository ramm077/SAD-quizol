package com.valuelabs.livequiz.service.question;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceExistsException;
import com.valuelabs.livequiz.model.entity.Option;
import com.valuelabs.livequiz.model.entity.Question;
import com.valuelabs.livequiz.model.entity.Quiz;
import com.valuelabs.livequiz.service.quiz.DisplayQuizService;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.valuelabs.livequiz.model.enums.QuestionType.SINGLE;
import static com.valuelabs.livequiz.model.enums.QuizType.OPEN_ENDED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateQuestionServiceTest {
    @Mock
    private DisplayQuestionService displayQuestionService;
    @Mock
    private QuestionService questionService;
    @Mock
    private DisplayQuizService displayQuizService;
    @Mock
    private AuthenticationService authenticationService;
    @InjectMocks
    private UpdateQuestionService updateQuestionService;

    @Test
    public void deleteQuestion_throwsInvalidResourceDetailsException(){
        Exception exception = assertThrows(InvalidResourceDetailsException.class,() -> updateQuestionService.deleteQuestion(null));
        assertEquals("questionId should be provided!",exception.getMessage());
    }
    @Test
    public void deleteQuestion_throwsResourceExistsException(){
//        when(authenticationService.getCurrentUserName()).thenReturn("ChandraMouli");
        Question question1 = new Question(1L,"Question 1",SINGLE, List.of(new Option(),new Option()),new Timestamp(System.currentTimeMillis()), authenticationService.getCurrentUserName(),new Timestamp(System.currentTimeMillis()),null,false );
        List<Question> questionList = new ArrayList<>(); questionList.add(question1);
        Quiz quiz = new Quiz(1L,"Quiz", OPEN_ENDED,questionList);
        when(displayQuizService.getAllQuizzes(false)).thenReturn(List.of(quiz));
        when(displayQuestionService.getQuestionById(1L,false)).thenReturn(question1);
        Exception exception = assertThrows(ResourceExistsException.class,() -> updateQuestionService.deleteQuestion(1L));
        assertEquals("Question with " + 1 + " is already mapped in Quiz " + quiz.getQuizName(),exception.getMessage());
    }
    @Test
    public void deleteQuestion_Success(){
        when(authenticationService.getCurrentUserName()).thenReturn("ChandraMouli");
        Question question1 = new Question(1L,"Question 1",SINGLE, List.of(new Option(),new Option()),new Timestamp(System.currentTimeMillis()), authenticationService.getCurrentUserName(),new Timestamp(System.currentTimeMillis()),null,false );
        Quiz quiz = new Quiz(1L,"Quiz", OPEN_ENDED,new ArrayList<>());
        when(displayQuestionService.getQuestionById(1L,false)).thenReturn(question1);
        when(displayQuizService.getAllQuizzes(false)).thenReturn(new ArrayList<>());
        updateQuestionService.deleteQuestion(1L);
        assertEquals(question1.getInActive(),true);
    }
}
