package com.valuelabs.livequiz.service.question;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.dto.response.DisplayOptionDTO;
import com.valuelabs.livequiz.model.dto.response.DisplayQuestionDTO;
import com.valuelabs.livequiz.model.entity.Question;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.repository.QuestionRepository;
import com.valuelabs.livequiz.service.option.DisplayOptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.valuelabs.livequiz.exception.ExceptionUtility.throwInvalidResourceDetailsException;
import static com.valuelabs.livequiz.exception.ExceptionUtility.throwResourceNotFoundException;

/**
 * Service class for displaying and managing questions.
 * @Service
 */
@Slf4j
@Service
public class DisplayQuestionService {
    private final QuestionRepository questionRepository;
    private final DisplayOptionService displayOptionService;
    /**
     * Constructs a DisplayQuestionService with the specified repositories and services.
     * @param questionRepository   Repository for handling questions.
     * @param displayOptionService Service for displaying options.
     */
    @Autowired
    public DisplayQuestionService(QuestionRepository questionRepository, DisplayOptionService displayOptionService) {
        this.questionRepository = questionRepository;
        this.displayOptionService = displayOptionService;
    }
    /**
     * Retrieves all questions based on the provided inactive status.
     * @param inActive Indicates whether to retrieve inactive or active questions.
     * @return List of questions based on the provided inactive status.
     * @throws ResourceNotFoundException if the no active questions are found.
     */
    public List<Question> getAllQuestionsByInActive(Boolean inActive) {
        log.info("Inside DisplayQuestionService, getAllQuestionsByInActive method!");
        log.debug("Trying to fetch all "+ (inActive ? "inactive" : "active") + " questions");
        if (!questionRepository.findAllByInActive(inActive).isEmpty()) {
            log.debug("Successfully fetched all "+ (inActive ? "inactive" : "active") + " questions, displaying questions");
            return questionRepository.findAllByInActive(inActive);
        } log.error("No "+ (inActive ? "inactive" : "active") +" questions are found, throwing ResourceNotFoundException!");
        throwResourceNotFoundException("Question","No "+(inActive ? "inactive" : "active") +"Questions are found!");
        return null;
    }

    /**
     * Retrieves all questions.
     * @return List of all questions.
     * @throws ResourceNotFoundException if the no questions are found.
     */
    public List<Question> getAllQuestions() {
        log.info("Inside DisplayQuestionService, getAllQuestions method!");
        if (!questionRepository.findAll().isEmpty()) {
            log.info("Successfully fetched all questions!, " + questionRepository.findAll().size() + " questions present");
            return questionRepository.findAll();
        }
        log.error("No questions are found, throwing ResourceNotFoundException!");
        throwResourceNotFoundException("Question","No Questions are found!");
        return null;
    }
    /**
     * Retrieves a question by its ID and inactive status.
     * @param questionId The ID of the question.
     * @param inActive   Indicates whether to retrieve an inactive or active question.
     * @return The question with the specified ID and inactive status.
     * @throws InvalidResourceDetailsException if the questionId is not given.
     * @throws ResourceNotFoundException if the active question is not found with questionId found.
     */
    public Question getQuestionById(Long questionId,Boolean inActive){
        log.info("Inside DisplayQuestionService, getQuestionById method!");
        if(questionId != null) {
            log.debug("Trying to fetch the question with questionId: "+questionId);
            return questionRepository.findByQuestionIdAndInActive(questionId,inActive)
                    .orElseThrow(() -> {throwResourceNotFoundException("Question","No "+(inActive ?
                            "inactive" : "active") +" Question found with " + questionId);
                        return null;});
        }
        log.error("Question Id not provided, throwing InvalidResourceDetailsException!");
        throwInvalidResourceDetailsException("Question","Question Id must be provided!");
        return null;
    }
    /**
     * Displays a question with associated options based on the provided ID and inactive status.
     * @param questionId The ID of the question.
     * @param inActive   Indicates whether to display an inactive or active question.
     * @return DisplayQuestionDTO containing question details and associated options.
     */
    public DisplayQuestionDTO displayQuestion(Long questionId, Boolean inActive){
        log.info("Inside DisplayQuestionService, displayQuestion method!");
        Question question = getQuestionById(questionId, inActive);
        List<DisplayOptionDTO> displayOptions = displayOptionService
                .displayOptionsInQuestion(question.getOptionList(),inActive);
        return new DisplayQuestionDTO(questionId,question.getQuestionText(),question.getQuestionType(),displayOptions);
    }
    /**
     * Displays a list of questions with associated options in a quiz based on the provided list and inactive status.
     * @param questionList List of questions to display.
     * @param inActive     Indicates whether to display inactive or active questions.
     * @return List of DisplayQuestionDTOs containing question details and associated options.
     */
    public List<DisplayQuestionDTO> displayQuestionsInQuiz(List<Question> questionList,Boolean inActive){
        log.info("Inside DisplayQuestionService, displayQuestionsInQuiz method!");
        if(questionList != null){
            log.debug("Trying to display all questions from questionList");
            return questionList.stream().map(question -> displayQuestion(question.getQuestionId(), inActive))
                    .collect(Collectors.toList());
        } else {
            log.info("questionList not provided!");
            return new ArrayList<>();
        }
    }
    /**
     * Displays all questions with associated options based on the provided inactive status.
     * @param inActive Indicates whether to display inactive or active questions.
     * @return List of DisplayQuestionDTOs containing question details and associated options.
     */
    public List<DisplayQuestionDTO> displayAllQuestions(Boolean inActive){
        log.info("Inside DisplayQuestionService, displayAllQuestions method!");
        return displayQuestionsInQuiz(getAllQuestionsByInActive(inActive),inActive);
    }
}
