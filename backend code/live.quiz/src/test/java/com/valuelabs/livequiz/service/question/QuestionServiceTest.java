package com.valuelabs.livequiz.service.question;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceExistsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.dto.request.LoginDTO;
import com.valuelabs.livequiz.model.dto.request.OptionCreationDTO;
import com.valuelabs.livequiz.model.dto.request.QuestionCreationDTO;
import com.valuelabs.livequiz.model.dto.request.UserCreationDTO;
import com.valuelabs.livequiz.model.dto.response.JWTAuthResponse;
import com.valuelabs.livequiz.model.entity.Option;
import com.valuelabs.livequiz.model.entity.Question;
import com.valuelabs.livequiz.model.entity.Quiz;
import com.valuelabs.livequiz.model.entity.User;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.model.enums.Role;
import com.valuelabs.livequiz.repository.QuestionRepository;
import com.valuelabs.livequiz.service.option.OptionService;
import com.valuelabs.livequiz.service.quiz.DisplayQuizService;
import com.valuelabs.livequiz.service.quiz.QuizService;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.valuelabs.livequiz.model.enums.QuestionType.*;
import static com.valuelabs.livequiz.model.enums.QuizType.OPEN_ENDED;
import static com.valuelabs.livequiz.model.enums.Role.RESPONDER;
import static com.valuelabs.livequiz.utils.InputValidator.validateDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private OptionService optionService;
    @Mock
    private DisplayQuestionService displayQuestionService;
    @Mock
    private DisplayQuizService displayQuizService;
    @Mock
    private QuizService quizService;
    @Mock
    private AuthenticationService authenticationService;
    @InjectMocks
    private QuestionService questionService;

    @Test
    public void createQuestion_AllInvalidOutputs(){
        QuestionCreationDTO questionDTO1 = new QuestionCreationDTO(null,"Question", List.of(new OptionCreationDTO("Option 1",true),new OptionCreationDTO("Option 2",false)));

        Exception exception1 = assertThrows(InvalidResourceDetailsException.class,() -> {validateDTO(questionDTO1);});
        assertEquals("questionType is required!", exception1.getMessage());

        QuestionCreationDTO questionDTO2 = new QuestionCreationDTO(SINGLE,null, List.of(new OptionCreationDTO("Option 1",true),new OptionCreationDTO("Option 2",false)));

        Exception exception2 = assertThrows(InvalidResourceDetailsException.class,() -> {validateDTO(questionDTO2);});
        assertEquals("questionText is required!", exception2.getMessage());

        QuestionCreationDTO questionDTO3 = new QuestionCreationDTO(SINGLE,"Question", null);

        Exception exception3 = assertThrows(InvalidResourceDetailsException.class,() -> {validateDTO(questionDTO3);});
        assertEquals("optionList is required!", exception3.getMessage());

        QuestionCreationDTO questionDTO4 = new QuestionCreationDTO(SINGLE,"Question", new ArrayList<>());
        validateDTO(questionDTO4);

        when(authenticationService.getCurrentUserName()).thenReturn("ChandraMouli");

        Question question = new Question(questionDTO4.questionType(),questionDTO4.questionText(),authenticationService.getCurrentUserName());

        Question result = null;

        try {
            result = questionService.createQuestion(questionDTO4);
        }catch (InvalidResourceDetailsException e){
            assertEquals("There should be atLeast 1 option present for single or multiple type question!",e.getMessage());
        }
    }

    @Test
    public void createQuestion_AllValidOutputs(){
        when(authenticationService.getCurrentUserName()).thenReturn("ChandraMouli");

        OptionCreationDTO optionDTO1 = new OptionCreationDTO("Option 1",true);
        Option option1 = new Option(1L,"Option 1",true, authenticationService.getCurrentUserName());
        when(optionService.createOption(optionDTO1)).thenReturn(option1);

        OptionCreationDTO optionDTO2 = new OptionCreationDTO("Option 2",false);
        Option option2 = new Option(2L,"Option 2",false, authenticationService.getCurrentUserName());
        when(optionService.createOption(optionDTO2)).thenReturn(option2);

        QuestionCreationDTO questionDTO1 = new QuestionCreationDTO(SINGLE,"Question", List.of(optionDTO1,optionDTO2));
        validateDTO(questionDTO1);

        Question question1 = new Question(questionDTO1.questionType(),questionDTO1.questionText(),authenticationService.getCurrentUserName());
        question1.setOptionList(List.of(option1,option2));

        when(questionRepository.save(question1)).thenReturn(question1);

        Question result1 = questionService.createQuestion(questionDTO1);
        assertEquals(result1,question1);

        QuestionCreationDTO questionDTO2 = new QuestionCreationDTO(TEXT,"Question",new ArrayList<>());
        validateDTO(questionDTO2);

        Question question2 = new Question(questionDTO2.questionType(),questionDTO2.questionText(),authenticationService.getCurrentUserName());

        when(questionRepository.save(question2)).thenReturn(question2);

        Question result2 = questionService.createQuestion(questionDTO2);
        assertEquals(result2,question2);
    }

    @Test
    public void createQuestionInQuiz_AllInvalidOutputs(){
        Quiz quiz = new Quiz(1L,"Quiz", OPEN_ENDED,new ArrayList<>());

        QuestionCreationDTO questionDTO1 = new QuestionCreationDTO(null,"Question", List.of(new OptionCreationDTO("Option 1",true),new OptionCreationDTO("Option 2",false)));

        Exception exception1 = assertThrows(InvalidResourceDetailsException.class,() -> {validateDTO(questionDTO1);});
        assertEquals("questionType is required!", exception1.getMessage());

        QuestionCreationDTO questionDTO2 = new QuestionCreationDTO(SINGLE,null, List.of(new OptionCreationDTO("Option 1",true),new OptionCreationDTO("Option 2",false)));

        Exception exception2 = assertThrows(InvalidResourceDetailsException.class,() -> {validateDTO(questionDTO2);});
        assertEquals("questionText is required!", exception2.getMessage());

        QuestionCreationDTO questionDTO3 = new QuestionCreationDTO(SINGLE,"Question", null);

        Exception exception3 = assertThrows(InvalidResourceDetailsException.class,() -> {validateDTO(questionDTO3);});
        assertEquals("optionList is required!", exception3.getMessage());

        Long quizId = null;

        OptionCreationDTO optionDTO1 = new OptionCreationDTO("Option 1",true);

        OptionCreationDTO optionDTO2 = new OptionCreationDTO("Option 2",false);

        QuestionCreationDTO questionDTO4 = new QuestionCreationDTO(SINGLE,"Question", List.of(optionDTO1,optionDTO2));
        validateDTO(questionDTO4);
        try{
            questionService.createQuestionInQuiz(quizId,questionDTO4);
        }catch (NullPointerException e){
            assertEquals("Cannot invoke \"com.valuelabs.livequiz.model.entity.Quiz.getQuestionList()\" because \"quiz\" is null",e.getMessage());
        }

        quizId = 2L;

        validateDTO(questionDTO4);
        try{
            questionService.createQuestionInQuiz(quizId,questionDTO4);
        }catch (NullPointerException e){
            assertEquals("Cannot invoke \"com.valuelabs.livequiz.model.entity.Quiz.getQuestionList()\" because \"quiz\" is null",e.getMessage());
        }

        quizId = 1L;
        QuestionCreationDTO questionDTO5 = new QuestionCreationDTO(SINGLE,"Question", new ArrayList<>());
        validateDTO(questionDTO5);

        when(displayQuizService.getQuizById(quizId,false)).thenReturn(quiz);

        try{
            questionService.createQuestionInQuiz(quizId,questionDTO5);
        }catch (InvalidResourceDetailsException e){
            assertEquals("There should be atLeast 1 option present for single or multiple type question!",e.getMessage());
        }
    }

    @Test
    public void createQuestionInQuiz_AllValidOutputs(){
        Quiz quiz = new Quiz(1L,"Quiz", OPEN_ENDED,new ArrayList<>());

        when(authenticationService.getCurrentUserName()).thenReturn("ChandraMouli");

        OptionCreationDTO optionDTO1 = new OptionCreationDTO("Option 1",true);
        Option option1 = new Option(1L,"Option 1",true, authenticationService.getCurrentUserName());
        when(optionService.createOption(optionDTO1)).thenReturn(option1);

        OptionCreationDTO optionDTO2 = new OptionCreationDTO("Option 2",false);
        Option option2 = new Option(2L,"Option 2",false, authenticationService.getCurrentUserName());
        when(optionService.createOption(optionDTO2)).thenReturn(option2);

        QuestionCreationDTO questionDTO1 = new QuestionCreationDTO(SINGLE,"Question", List.of(optionDTO1,optionDTO2));
        validateDTO(questionDTO1);

        Question question1 = new Question(questionDTO1.questionType(),questionDTO1.questionText(),authenticationService.getCurrentUserName());
        question1.setOptionList(List.of(option1,option2));

        when(questionRepository.save(question1)).thenReturn(question1);

        when(displayQuizService.getQuizById(1L,false)).thenReturn(quiz);
        when(quizService.saveQuiz(quiz)).thenReturn(quiz);
        questionService.createQuestionInQuiz(1L,questionDTO1);
        assertEquals(quiz.getQuestionList(),List.of(question1));


        QuestionCreationDTO questionDTO2 = new QuestionCreationDTO(TEXT,"Question",new ArrayList<>());
        validateDTO(questionDTO2);

        Question question2 = new Question(questionDTO2.questionType(),questionDTO2.questionText(),authenticationService.getCurrentUserName());

        when(questionRepository.save(question2)).thenReturn(question2);

        when(displayQuizService.getQuizById(1L,false)).thenReturn(quiz);
        when(quizService.saveQuiz(quiz)).thenReturn(quiz);
        questionService.createQuestionInQuiz(1L,questionDTO2);
        assertEquals(quiz.getQuestionList(),List.of(question1,question2));
    }

    @Test
    public void addQuestions_AllInvalidOutputs(){
        List<Long> questionIds1 = null;
        assertEquals(questionService.addQuestions(questionIds1),new ArrayList<>());
        List<Long> questionIds2 = new ArrayList<>();
        assertEquals(questionService.addQuestions(questionIds2),new ArrayList<>());
        List<Long> questionIds = List.of(1L);
        List<Question> questionList = new ArrayList<>();
        questionList.add(null);
        assertEquals(questionService.addQuestions(questionIds),questionList);
    }

    @Test
    public void addQuestions_AllValidOutputs(){
        when(authenticationService.getCurrentUserName()).thenReturn("ChandraMouli");
        List<Long> questionIds = List.of(1L,2L,3L);

        Question question1 = new Question(1L,"Question 1",SINGLE,List.of(new Option(),new Option()),new Timestamp(System.currentTimeMillis()), authenticationService.getCurrentUserName(),new Timestamp(System.currentTimeMillis()),null,false );
        when(displayQuestionService.getQuestionById(1L,false)).thenReturn(question1);

        Question question2 = new Question(2L,"Question 2",MULTIPLE,List.of(new Option(),new Option()),new Timestamp(System.currentTimeMillis()), authenticationService.getCurrentUserName(),new Timestamp(System.currentTimeMillis()),null,false );
        when(displayQuestionService.getQuestionById(2L,false)).thenReturn(question2);

        Question question3 = new Question(3L,"Question 3",TEXT,null,new Timestamp(System.currentTimeMillis()), authenticationService.getCurrentUserName(),new Timestamp(System.currentTimeMillis()),null,false );
        when(displayQuestionService.getQuestionById(3L,false)).thenReturn(question3);

        List<Question> expectedList = List.of(question1,question2,question3);

        List<Question> result = questionService.addQuestions(questionIds);
        assertEquals(result,expectedList);
    }

    @Test
    public void addQuestionsInQuiz_AllInvalidOutputs(){

        Question question1 = new Question(1L,"Question 1",SINGLE,List.of(new Option(),new Option()),new Timestamp(System.currentTimeMillis()), authenticationService.getCurrentUserName(),new Timestamp(System.currentTimeMillis()),null,false );
        when(displayQuestionService.getQuestionById(1L,false)).thenReturn(question1);

        Question question2 = new Question(2L,"Question 2",MULTIPLE,List.of(new Option(),new Option()),new Timestamp(System.currentTimeMillis()), authenticationService.getCurrentUserName(),new Timestamp(System.currentTimeMillis()),null,false );

        Quiz quiz = new Quiz(1L,"Quiz", OPEN_ENDED,List.of(question1));

        when(displayQuizService.getQuizById(1L,false)).thenReturn(quiz);
        when(displayQuizService.getQuizById(null,false)).thenReturn(null);

        try {
            questionService.addQuestionsInQuiz(null,List.of(1L));
        }catch (NullPointerException e){
            assertEquals("Cannot invoke \"com.valuelabs.livequiz.model.entity.Quiz.getQuestionList()\" because \"quiz\" is null",e.getMessage());
        }

        List<Long> questionIds1 = null;
        try {
            questionService.addQuestionsInQuiz(1L,questionIds1);
        }catch (InvalidResourceDetailsException e){
            assertEquals("Question Ids must be provided!",e.getMessage());
        }

        List<Long> questionIds2 = new ArrayList<>();
        try {
            questionService.addQuestionsInQuiz(1L,questionIds2);
        }catch (InvalidResourceDetailsException e){
            assertEquals("Question Ids must be provided!",e.getMessage());
        }

        List<Long> questionIds3 = List.of(1L,2L);
        try {
            questionService.addQuestionsInQuiz(1L,questionIds3);
        }catch (ResourceExistsException e){
            assertEquals("Question with questionId: " + 1 + " is already present in Quiz with quizId: " + 1,e.getMessage());
        }
    }

    @Test
    public void addQuestionsInQuiz_AllValidOutputs(){
        when(authenticationService.getCurrentUserName()).thenReturn("ChandraMouli");
        List<Long> questionIds = List.of(1L,2L,3L);

        Question question1 = new Question(1L,"Question 1",SINGLE,List.of(new Option(),new Option()),new Timestamp(System.currentTimeMillis()), authenticationService.getCurrentUserName(),new Timestamp(System.currentTimeMillis()),null,false );
        when(displayQuestionService.getQuestionById(1L,false)).thenReturn(question1);

        Question question2 = new Question(2L,"Question 2",MULTIPLE,List.of(new Option(),new Option()),new Timestamp(System.currentTimeMillis()), authenticationService.getCurrentUserName(),new Timestamp(System.currentTimeMillis()),null,false );
        when(displayQuestionService.getQuestionById(2L,false)).thenReturn(question2);

        Question question3 = new Question(3L,"Question 3",TEXT,null,new Timestamp(System.currentTimeMillis()), authenticationService.getCurrentUserName(),new Timestamp(System.currentTimeMillis()),null,false );
        when(displayQuestionService.getQuestionById(3L,false)).thenReturn(question3);

        List<Question> expectedList = List.of(question1,question2,question3);

        Quiz quiz = new Quiz(1L,"Quiz", OPEN_ENDED,new ArrayList<>());

        when(displayQuizService.getQuizById(1L,false)).thenReturn(quiz);
        when(quizService.saveQuiz(quiz)).thenReturn(quiz);

        questionService.addQuestionsInQuiz(1L,questionIds);
        assertEquals(quiz.getQuestionList(),expectedList);
    }
    @Test
    public void deleteQuestionInQuiz_throwsResourceNotFoundException(){
        when(authenticationService.getCurrentUserName()).thenReturn("ChandraMouli");
        Question question1 = new Question(1L,"Question 1",SINGLE,List.of(new Option(),new Option()),new Timestamp(System.currentTimeMillis()), authenticationService.getCurrentUserName(),new Timestamp(System.currentTimeMillis()),null,false );
        Quiz quiz = new Quiz(1L,"Quiz", OPEN_ENDED,new ArrayList<>());
        when(displayQuizService.getQuizById(1L,false)).thenReturn(quiz);
        when(displayQuestionService.getQuestionById(1L,false)).thenReturn(question1);
        Exception exception = assertThrows(ResourceNotFoundException.class,() -> questionService.deleteQuestionInQuiz(1L,1L));
        assertEquals("Question with questionId : " + 1 +
                " not found in the given Quiz with quizId : " + 1,exception.getMessage());
    }
    @Test
    public void deleteQuestionInQuiz_Success(){
        when(authenticationService.getCurrentUserName()).thenReturn("ChandraMouli");
        Question question1 = new Question(1L,"Question 1",SINGLE,List.of(new Option(),new Option()),new Timestamp(System.currentTimeMillis()), authenticationService.getCurrentUserName(),new Timestamp(System.currentTimeMillis()),null,false );
        List<Question> questionList = new ArrayList<>(); questionList.add(question1);
        Quiz quiz = new Quiz(1L,"Quiz", OPEN_ENDED,questionList);
        when(displayQuizService.getQuizById(1L,false)).thenReturn(quiz);
        when(displayQuestionService.getQuestionById(1L,false)).thenReturn(question1);
        when(quizService.saveQuiz(quiz)).thenReturn(quiz);
        questionService.deleteQuestionInQuiz(1L,1L);
        assertEquals(new ArrayList<>(),quiz.getQuestionList());
    }
    @Test
    public void saveQuestion_throwsResourceNotFoundException(){
        Exception exception = assertThrows(ResourceNotFoundException.class,() -> questionService.saveQuestion(null));
        assertEquals("Can't save, Question not found!",exception.getMessage());
    }
    @Test
    public void saveQuestion_Success(){
        Question question = new Question();
        when(questionRepository.save(question)).thenReturn(question);
        questionService.saveQuestion(question);
    }
}
