package com.valuelabs.livequiz.service.question;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceExistsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.dto.request.QuestionCreationDTO;
import com.valuelabs.livequiz.model.entity.Option;
import com.valuelabs.livequiz.model.entity.Question;
import com.valuelabs.livequiz.model.entity.Quiz;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.repository.QuestionRepository;
import com.valuelabs.livequiz.service.option.OptionService;
import com.valuelabs.livequiz.service.quiz.DisplayQuizService;
import com.valuelabs.livequiz.service.quiz.QuizService;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.valuelabs.livequiz.exception.ExceptionUtility.*;
import static com.valuelabs.livequiz.utils.InputValidator.validateDTO;
/**
 * Service class for managing and manipulating questions.
 * @Service
 */
@Slf4j
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final OptionService optionService;
    private final DisplayQuestionService displayQuestionService;
    private final DisplayQuizService displayQuizService;
    private final QuizService quizService;
    private final AuthenticationService authenticationService;
    /**
     * Constructs a QuestionService with the specified repositories and services.
     *
     * @param questionRepository     Repository for handling questions.
     * @param optionService          Service for managing options.
     * @param displayQuestionService Service for displaying questions.
     * @param displayQuizService     Service for displaying quizzes.
     * @param quizService            Service for managing quizzes.
     * @param authenticationService  used to fetch the username form the security context.
     */
    @Autowired
    public QuestionService(QuestionRepository questionRepository, OptionService optionService, DisplayQuestionService displayQuestionService, DisplayQuizService displayQuizService, QuizService quizService, AuthenticationService authenticationService) {
        this.questionRepository = questionRepository;
        this.optionService = optionService;
        this.displayQuestionService = displayQuestionService;
        this.displayQuizService = displayQuizService;
        this.quizService = quizService;
        this.authenticationService = authenticationService;
    }
    /**
     * Creates a new question based on the provided QuestionCreationDTO.
     * @param questionDTO The data transfer object containing question details.
     * @return The created Question.
     */
    public Question createQuestion(QuestionCreationDTO questionDTO)  {
        log.info("Inside DisplayQuestionService, createQuestion method!,validating the QuestionCreationDTO!");
        validateDTO(questionDTO);
        Question question = new Question(questionDTO.questionType(),questionDTO.questionText(),authenticationService.getCurrentUserName());
        if(questionDTO.questionType().equals(QuestionType.MULTIPLE) || questionDTO.questionType().equals(QuestionType.SINGLE)) {
            if(questionDTO.optionList() != null && !questionDTO.optionList().isEmpty()) {
                List<Option> optionList = questionDTO.optionList().stream().map(optionService::createOption).collect(Collectors.toList());
                question.setOptionList(optionList);
            } else throwInvalidResourceDetailsException("Question", "There should be atLeast 1 option present for single or multiple type question!");
        } questionRepository.save(question);
        log.debug("Question saved successfully with question Id: " + question.getQuestionId());
        return question;
    }
    /**
     * Creates and adds a question to a specific quiz.
     * @param quizId      The ID of the quiz.
     * @param questionDTO The data transfer object containing question details.
     * @throws InvalidResourceDetailsException if quizId,questionDTO fields are null.
     */
    public void createQuestionInQuiz(Long quizId, QuestionCreationDTO questionDTO) {
        log.info("Inside DisplayQuestionService, createQuestion method!,validating the QuestionCreationDTO!");
        validateDTO(questionDTO);
        log.debug("Trying to fetch the quiz with Id: "+ quizId);
        Quiz quiz = displayQuizService.getQuizById(quizId,false);
        quiz.getQuestionList().add(createQuestion(questionDTO));
        quiz.setUpdatedBy(authenticationService.getCurrentUserName());
        quizService.saveQuiz(quiz);
    }
    /**
     * Adds questions to the database based on the provided list of question IDs.
     * @param questionIds List of question IDs to add.
     * @return List of added questions.
     */
    public List<Question> addQuestions(List<Long> questionIds){
        log.info("Inside DisplayQuestionService, addQuestions method!");
        if(questionIds != null && !questionIds.isEmpty()) {
            log.debug("Trying to fetch "+ questionIds.size() + " number of questions");
            return questionIds.stream().map(questionId ->
                    displayQuestionService.getQuestionById(questionId, false)).collect(Collectors.toList());
        }
        log.info("No questionIds, returning empty List");
        return new ArrayList<>();
    }
    /**
     * Adds questions to a specific quiz based on the provided list of question IDs.
     * @param quizId      The ID of the quiz.
     * @param questionIds List of question IDs to add to the quiz.
     * @throws InvalidResourceDetailsException if quizId,questionIds are not provided
     * @throws ResourceExistsException if the question with questionId is already in the Quiz.
     */
    public void addQuestionsInQuiz(Long quizId, List<Long> questionIds){
        log.info("Inside DisplayQuestionService, addQuestionsInQuiz method!");
        Quiz quiz = displayQuizService.getQuizById(quizId,false);
        if(questionIds != null && !questionIds.isEmpty()){
            questionIds.forEach(questionId -> {
                if (!quiz.getQuestionList().contains(displayQuestionService.getQuestionById(questionId, false))) {
                    quiz.getQuestionList().add(displayQuestionService.getQuestionById(questionId, false));
                    quiz.setUpdatedBy(authenticationService.getCurrentUserName());
                    quizService.saveQuiz(quiz);
                } else throwResourceExistsException("Question","Question with questionId: " + questionId + " is already present in Quiz with quizId: " + quizId);});
        } else throwInvalidResourceDetailsException("Question","Question Ids must be provided!");
    }
    /**
     * Deletes a question from a specific quiz.
     * @param quizId     The ID of the quiz.
     * @param questionId The ID of the question to delete.
     * @throws InvalidResourceDetailsException if quizId,questionId are not provided
     * @throws ResourceNotFoundException if the question is not found in the Quiz.
     */
    public void deleteQuestionInQuiz(Long quizId,Long questionId){
        log.info("Inside DisplayQuestionService, deleteQuestionInQuiz method!");
        Quiz quiz = displayQuizService.getQuizById(quizId,false);
        Question question = displayQuestionService.getQuestionById(questionId,false);
        log.debug("Successfully fetched the question "+question.getQuestionText()+ " and quiz "+quiz.getQuizName()+" and trying to delete the question in quiz");
        if(quiz.getQuestionList().contains(question)) {
            log.debug("Successfully found the question "+question.getQuestionText() + " inside quiz "+quiz.getQuizName()+" and ready for deletion");
            quiz.getQuestionList().remove(question);
            quiz.setUpdatedBy(authenticationService.getCurrentUserName());
            quizService.saveQuiz(quiz);
            log.debug("Question "+question.getQuestionText() + " is successfully removed in quiz "+quiz.getQuizName());
        } else {
            log.error("Question with questionId : " + questionId +" not found in the given Quiz with quizId : " + quizId);
            throwResourceNotFoundException("Question","Question with questionId : " + questionId +
                    " not found in the given Quiz with quizId : " + quizId);
        }
    }
    /**
     * Saves a question to the repository.
     * @param question The question to be saved.
     * @throws ResourceNotFoundException if the question is not found to save.
     */
    public void saveQuestion(Question question){
        if(question != null) {
            log.debug("Saving question" +question.getQuestionText()+" in database");
            questionRepository.save(question);
        }else throwResourceNotFoundException("Question","Can't save, Question not found!");
    }
}