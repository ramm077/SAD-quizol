package com.valuelabs.livequiz.service.question;
import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceExistsException;
import com.valuelabs.livequiz.model.entity.Question;
import com.valuelabs.livequiz.service.quiz.DisplayQuizService;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.valuelabs.livequiz.exception.ExceptionUtility.*;

/**
 * Service class for updating and validating questions.
 * @Service
 */
@Service
@Slf4j
public class UpdateQuestionService {
    private final DisplayQuestionService displayQuestionService;
    private final QuestionService questionService;
    private final DisplayQuizService displayQuizService;
    private final AuthenticationService authenticationService;
    /**
     * Constructs an UpdateQuestionService with the specified services.
     * @param displayQuestionService Service for displaying questions.
     * @param questionService        Service for managing questions.
     * @param displayQuizService     Service for displaying quizzes.
     * @param authenticationService  Service for fetching user details from security context
     */
    @Autowired
    public UpdateQuestionService(DisplayQuestionService displayQuestionService, QuestionService questionService, DisplayQuizService displayQuizService, AuthenticationService authenticationService) {
        this.displayQuestionService = displayQuestionService;
        this.questionService = questionService;
        this.displayQuizService = displayQuizService;
        this.authenticationService = authenticationService;
    }
    /**
     * Deletes a question based on the provided question ID.
     * @param questionId The ID of the question to delete.
     * @throws InvalidResourceDetailsException if the questionId is not provided.
     * @throws ResourceExistsException if the question with questionId is already mapped with any quiz.
     */
    public void deleteQuestion(Long questionId){
        log.info("Inside UpdateQuestionService,deleteQuestion method!");
        if(questionId != null) {
            Question question = displayQuestionService.getQuestionById(questionId, false);
            displayQuizService.getAllQuizzes(false).stream().filter(quiz -> quiz.getQuestionList().contains(question)).forEach(quiz -> {
                log.debug("Question with questionId: "+questionId+" is already mapped in Quiz " + quiz.getQuizName());
                throwResourceExistsException("Question", "Question with " + questionId +
                        " is already mapped in Quiz " + quiz.getQuizName());
            });
            question.setUpdatedBy(authenticationService.getCurrentUserName());
            question.setInActive(true);
            questionService.saveQuestion(question);
            log.debug("Question with questionId: "+questionId+" is successfully deleted!");
        }else {
            log.error("Question Id is not provided!, throwing InvalidResourceDetailsException");
            throwInvalidResourceDetailsException("Question","questionId should be provided!");
        }
    }
}
