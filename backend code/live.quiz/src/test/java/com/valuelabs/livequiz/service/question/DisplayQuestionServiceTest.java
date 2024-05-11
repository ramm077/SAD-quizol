package com.valuelabs.livequiz.service.question;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.dto.response.DisplayOptionDTO;
import com.valuelabs.livequiz.model.dto.response.DisplayQuestionDTO;
import com.valuelabs.livequiz.model.entity.Option;
import com.valuelabs.livequiz.model.entity.Question;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.repository.QuestionRepository;
import com.valuelabs.livequiz.service.option.DisplayOptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.valuelabs.livequiz.model.enums.QuestionType.SINGLE;
import static com.valuelabs.livequiz.model.enums.QuestionType.TEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DisplayQuestionServiceTest {
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private DisplayOptionService displayOptionService;
    @InjectMocks
    private DisplayQuestionService displayQuestionService;

    @Test
    public void testGetAllQuestionsByInActive_whenInActiveIsTrue() {
        // given
        Boolean inActive = true;
        Question question1 = new Question(1L,"Question 1",QuestionType.TEXT,List.of(new Option()),new Timestamp(System.currentTimeMillis()),"Default",null,null,true);
        Question question2 = new Question(1L,"Question 1",QuestionType.TEXT,List.of(new Option()),new Timestamp(System.currentTimeMillis()),"Default",null,null,true);
        List<Question> expectedQuestions = List.of(question1,question2);

        // when
        when(questionRepository.findAllByInActive(inActive)).thenReturn(expectedQuestions);
        List<Question> actualQuestions = displayQuestionService.getAllQuestionsByInActive(inActive);

        // then
        assertEquals(expectedQuestions, actualQuestions);
        verify(questionRepository, times(2)).findAllByInActive(inActive);
    }

    @Test
    public void testGetAllQuestionsByInActive_whenInActiveIsFalse() {
        // given
        Boolean inActive = false;
        List<Question> expectedQuestions = Arrays.asList(new Question(QuestionType.TEXT, "Question 1"), new Question(QuestionType.TEXT, "Question 2"));

        // when
        when(questionRepository.findAllByInActive(inActive)).thenReturn(expectedQuestions);
        List<Question> actualQuestions = displayQuestionService.getAllQuestionsByInActive(inActive);

        // then
        assertEquals(expectedQuestions, actualQuestions);
        verify(questionRepository, times(2)).findAllByInActive(inActive);
    }

    @Test
    public void testGetAllQuestionsByInActive_whenNoQuestionsFound() {
        // given
        Boolean inActive = true;

        // when
        when(questionRepository.findAllByInActive(inActive)).thenReturn(Collections.emptyList());
        List<Question> actualQuestions = Collections.emptyList();
        try {
            actualQuestions = displayQuestionService.getAllQuestionsByInActive(inActive);
        } catch (ResourceNotFoundException e) {
            System.out.println(e.getMessage());
        }
        // then
        assertEquals(Collections.emptyList(), actualQuestions);
        verify(questionRepository, times(1)).findAllByInActive(inActive);
    }

    @Test
    public void testGetAllQuestions_WithQuestions() {
        // Arrange
        Question question1 = new Question(1L,"Question 1",QuestionType.TEXT,List.of(new Option()),new Timestamp(System.currentTimeMillis()),"Default",null,null,true);
        Question question2 = new Question(1L,"Question 2",QuestionType.TEXT,List.of(new Option()),new Timestamp(System.currentTimeMillis()),"Default",null,null,true);
        List<Question> questions = Arrays.asList(question1, question2);
        when(questionRepository.findAll()).thenReturn(questions);

        // Act
        List<Question> allQuestions = displayQuestionService.getAllQuestions();

        // Assert
        assertEquals(2, allQuestions.size());
        assertEquals(question1, allQuestions.get(0));
        assertEquals(question2, allQuestions.get(1));
    }

    @Test
    public void testGetAllQuestions_NoQuestions() {
        // Arrange
        when(questionRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Question> actualQuestions = Collections.emptyList();
        try {
            actualQuestions = displayQuestionService.getAllQuestions();
        }catch (ResourceNotFoundException e) {
            System.out.println(e.getMessage());
        }
        assertTrue(actualQuestions.isEmpty());
        verify(questionRepository, times(1)).findAll();

    }

    @Test
    void testGetQuestionById_ValidQuestionId() {
        // Arrange
        Long questionId = 1L;
        Boolean inActive = false;
        Question expectedQuestion = new Question(1L,"Question 1",QuestionType.TEXT,List.of(new Option()),new Timestamp(System.currentTimeMillis()),"Default",null,null,false);
        when(questionRepository.findByQuestionIdAndInActive(questionId, inActive)).thenReturn(Optional.of(expectedQuestion));

        // Act
        Question actualQuestion = displayQuestionService.getQuestionById(questionId, inActive);

        // Assert
        assertEquals(expectedQuestion, actualQuestion);
    }

    @Test
    void testGetQuestionById_InvalidQuestionId() {
        // Arrange
        Long questionId = 1L;
        Boolean inActive = false;
        when(questionRepository.findByQuestionIdAndInActive(questionId, inActive)).thenReturn(Optional.empty());

        // Act
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            displayQuestionService.getQuestionById(questionId, inActive);
        });

        // Assert
        assertEquals("No active Question found with 1", exception.getMessage());
    }

    @Test
    void testGetQuestionById_NoQuestionId() {
        // Arrange
        Long questionId = null;
        Boolean inActive = false;

        // Act
        Exception exception = assertThrows(InvalidResourceDetailsException.class, () -> {
            displayQuestionService.getQuestionById(questionId, inActive);
        });

        // Assert
        assertEquals("Question Id must be provided!", exception.getMessage());
    }

    @Test
    public void testDisplayQuestion() {
        // Given
        Long questionId = 1L;
        Boolean inActive = false;
        Option option1 = new Option(1L,"Option 1",new Timestamp(System.currentTimeMillis()),"default",null,null,false,true,null);
        Option option2 = new Option(2L,"Option 2",new Timestamp(System.currentTimeMillis()),"default",null,null,false,false,null);
        Question question = new Question(1L,"What is your name?", SINGLE,List.of(option1,option2),new Timestamp(System.currentTimeMillis()),"Default",null,null,false);
        when(questionRepository.findByQuestionIdAndInActive(questionId, inActive)).thenReturn(Optional.of(question));
        DisplayOptionDTO optionDTO1 = new DisplayOptionDTO(1L,"22",true);
        DisplayOptionDTO optionDTO2 = new DisplayOptionDTO(2L,"21",false);
        List<DisplayOptionDTO> displayOptions = List.of(optionDTO1,optionDTO2);
        when(displayOptionService.displayOptionsInQuestion(question.getOptionList(),inActive)).thenReturn(displayOptions);

        // When
        DisplayQuestionDTO displayQuestion = displayQuestionService.displayQuestion(questionId, inActive);
        DisplayQuestionDTO expectedQuestion = new DisplayQuestionDTO(1L,"What is your name?",SINGLE,displayOptions);
        // Then
        assertThat(displayQuestion).isEqualTo(expectedQuestion);
    }

    @Test
    public void testDisplayQuestion_QuestionNotFound() {
        // Given
        Long questionId = 1L;
        Boolean inActive = false;
        Question question = null;

        // When
        DisplayQuestionDTO displayQuestion = null;
        try {
            displayQuestion = displayQuestionService.displayQuestion(questionId,inActive);
        }catch (ResourceNotFoundException e){
            System.out.println(e.getMessage());
        }
        // Then
        assertNull(displayQuestion);
    }

    @Test
    public void testDisplayQuestionsInQuiz_NullQuestionList() {
        // Given
        List<Question> questionList = null;
        Boolean inActive = false;

        // When
        List<DisplayQuestionDTO> result = displayQuestionService.displayQuestionsInQuiz(questionList, inActive);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    public void testDisplayQuestionsInQuiz_InActiveFalse() {
        // Given
        Option option1 = new Option(1L,"22",new Timestamp(System.currentTimeMillis()),"default",null,null,false,true,null);
        Option option2 = new Option(2L,"21",new Timestamp(System.currentTimeMillis()),"default",null,null,false,false,null);
        List<Option> optionList = List.of(option1,option2);
        Question question1 = new Question(1L,"What is your name?", TEXT,null,new Timestamp(System.currentTimeMillis()),"Default",null,null,false);
        DisplayQuestionDTO questionDTO1 = new DisplayQuestionDTO(1L,"What is your name?",TEXT,null);
        when(questionRepository.findByQuestionIdAndInActive(question1.getQuestionId(),false)).thenReturn(Optional.of(question1));
        when(displayOptionService
                .displayOptionsInQuestion(question1.getOptionList(),false)).thenReturn(null);


        Question question2 = new Question(2L,"What is your age?", SINGLE,optionList,new Timestamp(System.currentTimeMillis()),"Default",null,null,false);
        when(questionRepository.findByQuestionIdAndInActive(question2.getQuestionId(),false)).thenReturn(Optional.of(question2));
        DisplayOptionDTO optionDTO1 = new DisplayOptionDTO(1L,"22",true);
        DisplayOptionDTO optionDTO2 = new DisplayOptionDTO(2L,"21",false);
        List<DisplayOptionDTO> displayOptions = List.of(optionDTO1,optionDTO2);
        DisplayQuestionDTO questionDTO2 = new DisplayQuestionDTO(2L,"What is your age?",SINGLE,displayOptions);

        when(displayOptionService
                .displayOptionsInQuestion(question2.getOptionList(),false)).thenReturn(displayOptions);

        Boolean inActive = false;
        List<Question> questionList = List.of(question1,question2);

        // When
        List<DisplayQuestionDTO> result = displayQuestionService.displayQuestionsInQuiz(questionList, inActive);
        List<DisplayQuestionDTO> expectedResult = List.of(questionDTO1,questionDTO2);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void testDisplayQuestionsInQuiz_InActiveTrue() {
        // Given
        Option option1 = new Option(1L,"22",new Timestamp(System.currentTimeMillis()),"default",null,null,true,true,null);
        Option option2 = new Option(2L,"21",new Timestamp(System.currentTimeMillis()),"default",null,null,true,false,null);
        List<Option> optionList = List.of(option1,option2);
        Question question1 = new Question(1L,"What is your name?", TEXT,null,new Timestamp(System.currentTimeMillis()),"Default",null,null,true);
        DisplayQuestionDTO questionDTO1 = new DisplayQuestionDTO(1L,"What is your name?",TEXT,null);
        when(questionRepository.findByQuestionIdAndInActive(question1.getQuestionId(),true)).thenReturn(Optional.of(question1));
        when(displayOptionService
                .displayOptionsInQuestion(question1.getOptionList(),true)).thenReturn(null);


        Question question2 = new Question(2L,"What is your age?", SINGLE,optionList,new Timestamp(System.currentTimeMillis()),"Default",null,null,true);
        when(questionRepository.findByQuestionIdAndInActive(question2.getQuestionId(),true)).thenReturn(Optional.of(question2));
        DisplayOptionDTO optionDTO1 = new DisplayOptionDTO(1L,"22",true);
        DisplayOptionDTO optionDTO2 = new DisplayOptionDTO(2L,"21",false);
        List<DisplayOptionDTO> displayOptions = List.of(optionDTO1,optionDTO2);
        DisplayQuestionDTO questionDTO2 = new DisplayQuestionDTO(2L,"What is your age?",SINGLE,displayOptions);

        when(displayOptionService
                .displayOptionsInQuestion(question2.getOptionList(),true)).thenReturn(displayOptions);

        Boolean inActive = true;
        List<Question> questionList = List.of(question1,question2);

        // When
        List<DisplayQuestionDTO> result = displayQuestionService.displayQuestionsInQuiz(questionList, inActive);
        List<DisplayQuestionDTO> expectedResult = List.of(questionDTO1,questionDTO2);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void displayAllQuestions_InActiveFalse(){
        Option option1 = new Option(1L,"22",new Timestamp(System.currentTimeMillis()),"default",null,null,false,true,null);
        Option option2 = new Option(2L,"21",new Timestamp(System.currentTimeMillis()),"default",null,null,false,false,null);
        List<Option> optionList = List.of(option1,option2);
        Question question1 = new Question(1L,"What is your name?", TEXT,null,new Timestamp(System.currentTimeMillis()),"Default",null,null,false);
        DisplayQuestionDTO questionDTO1 = new DisplayQuestionDTO(1L,"What is your name?",TEXT,null);
        when(questionRepository.findByQuestionIdAndInActive(question1.getQuestionId(),false)).thenReturn(Optional.of(question1));
        when(displayOptionService
                .displayOptionsInQuestion(question1.getOptionList(),false)).thenReturn(null);


        Question question2 = new Question(2L,"What is your age?", SINGLE,optionList,new Timestamp(System.currentTimeMillis()),"Default",null,null,false);
        when(questionRepository.findByQuestionIdAndInActive(question2.getQuestionId(),false)).thenReturn(Optional.of(question2));
        DisplayOptionDTO optionDTO1 = new DisplayOptionDTO(1L,"22",true);
        DisplayOptionDTO optionDTO2 = new DisplayOptionDTO(2L,"21",false);
        List<DisplayOptionDTO> displayOptions = List.of(optionDTO1,optionDTO2);
        DisplayQuestionDTO questionDTO2 = new DisplayQuestionDTO(2L,"What is your age?",SINGLE,displayOptions);

        when(displayOptionService
                .displayOptionsInQuestion(question2.getOptionList(),false)).thenReturn(displayOptions);

        Boolean inActive = false;
        List<Question> questionList = List.of(question1,question2);
        when(questionRepository.findAllByInActive(Mockito.any(Boolean.class))).thenReturn(questionList);

        // When
        List<DisplayQuestionDTO> result = displayQuestionService.displayAllQuestions(inActive);
        List<DisplayQuestionDTO> expectedResult = List.of(questionDTO1,questionDTO2);
        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).isEqualTo(expectedResult);
    }
}
