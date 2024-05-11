package com.valuelabs.livequiz.service.quiz;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.dto.response.DisplayQuestionDTO;
import com.valuelabs.livequiz.model.dto.response.DisplayQuizDTO;
import com.valuelabs.livequiz.model.dto.response.QuizDisplayDTO;
import com.valuelabs.livequiz.model.entity.Quiz;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.repository.QuizRepository;
import com.valuelabs.livequiz.service.question.DisplayQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.valuelabs.livequiz.exception.ExceptionUtility.throwInvalidResourceDetailsException;
import static com.valuelabs.livequiz.exception.ExceptionUtility.throwResourceNotFoundException;
import static com.valuelabs.livequiz.utils.ResourceValidator.validateResource;
/**
 * Service class for displaying quiz-related information.
 */
@Service
@Slf4j
public class DisplayQuizService {
    private final QuizRepository quizRepository;
    private final DisplayQuestionService displayQuestionService;
    /**
     * Constructor for DisplayQuizService.
     * @param quizRepository           Repository for Quiz entities.
     * @param displayQuestionService   Service for displaying questions.
     */
    @Autowired
    public DisplayQuizService(QuizRepository quizRepository, DisplayQuestionService displayQuestionService) {
        this.quizRepository = quizRepository;
        this.displayQuestionService = displayQuestionService;
    }
    /**
     * Retrieves a quiz by its ID and active status.
     * @param quizId    The ID of the quiz to retrieve.
     * @param inActive  Boolean indicating whether to include inactive quizzes.
     * @return          The retrieved Quiz entity.
     * @throws ResourceNotFoundException if the quiz is not found.
     * @throws InvalidResourceDetailsException if quizId is not provided.
     */
    public Quiz getQuizById(Long quizId, Boolean inActive){
        log.info("Inside DisplayQuizService, getQuizById method!");
        if(quizId != null){
            return quizRepository.findByQuizIdAndInActive(quizId,inActive).orElseThrow(() -> {
                log.error(((inActive) ? "inactive" : "active") + "Quiz not found with quizId: " + quizId);
                throwResourceNotFoundException("Quiz", ((inActive) ? "inactive" : "active") + " Quiz not found with quizId: " + quizId);
                return null;
            });
        } log.error("Quiz Id is not provided!, throwing InvalidResourceDetailsException");
        throwInvalidResourceDetailsException("Quiz","QuizId must be provided!");
        return null;
    }
    /**
     * Displays information about a quiz, including its questions.
     * @param quizId    The ID of the quiz to display.
     * @param inActive  Boolean indicating whether to include inactive quizzes.
     * @return          DisplayQuizDTO containing quiz information and questions.
     */
    public DisplayQuizDTO displayQuiz(Long quizId, Boolean inActive){
        log.info("Inside DisplayQuizService, displayQuiz method!");
        Quiz quiz = getQuizById(quizId, inActive);
        List<DisplayQuestionDTO> displayQuestions = displayQuestionService.displayQuestionsInQuiz(quiz.getQuestionList(),inActive);
        log.debug("Successfully fetched the quiz "+quiz.getQuizName()+" ready to display");
        return new DisplayQuizDTO(quizId, quiz.getQuizName(), quiz.getQuizType(),displayQuestions);
    }
    /**
     * Retrieves all quizzes with optional filtering by active status.
     * @param inActive  Boolean indicating whether to include inactive quizzes.
     * @return          List of Quiz entities.
     * @throws          ResourceNotFoundException if no quizzes are found.
     */
    public List<Quiz> getAllQuizzes(Boolean inActive){
        log.info("Inside DisplayQuizService, getAllQuizzes method!");
        validateResource(List.of("Quiz","quizzes",quizRepository.findAllByInActive(inActive)),inActive);
        log.debug(quizRepository.findAllByInActive(inActive).size()+" quizzes found!, ready to display");
        return quizRepository.findAllByInActive(inActive);
    }
    /**
     * Retrieves all quizzes of a specific type with optional filtering by active status.
     * @param quizType  The type of quizzes to retrieve.
     * @param inActive  Boolean indicating whether to include inactive quizzes.
     * @return          List of Quiz entities of the specified type.
     * @throws          ResourceNotFoundException if no quizzes are found.
     */
    public List<Quiz> getAllQuizzes(QuizType quizType, Boolean inActive){
        validateResource(List.of("Quiz",quizType + " quizzes",quizRepository.findAllByQuizTypeAndInActive(quizType,inActive)),inActive);
        return quizRepository.findAllByQuizTypeAndInActive(quizType,inActive);
    }
    /**
     * Displays information about all quizzes with optional filtering by active status.
     * @param inActive  Boolean indicating whether to include inactive quizzes.
     * @return          List of QuizDisplayDTO containing basic quiz information.
     */
    public List<QuizDisplayDTO> displayAllQuizzes(Boolean inActive){
        return getAllQuizzes(inActive).stream().map(quiz ->
                new QuizDisplayDTO(quiz.getQuizId(), quiz.getQuizName(), quiz.getQuizType()))
                .collect(Collectors.toList());
    }
    /**
     * Displays information about all quizzes of a specific type with optional filtering by active status.
     * @param quizType  The type of quizzes to display.
     * @param inActive  Boolean indicating whether to include inactive quizzes.
     * @return          List of QuizDisplayDTO containing basic quiz information.
     */
    public List<QuizDisplayDTO> displayAllTypeQuizzes(QuizType quizType, boolean inActive) {
        return getAllQuizzes(quizType,inActive).stream().map(quiz ->
                        new QuizDisplayDTO(quiz.getQuizId(), quiz.getQuizName(), quiz.getQuizType()))
                .collect(Collectors.toList());
    }
}
