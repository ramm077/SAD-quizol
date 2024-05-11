package com.valuelabs.livequiz.service.quiz;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceExistsException;
import com.valuelabs.livequiz.model.dto.request.QuizCreationDTO;
import com.valuelabs.livequiz.model.dto.request.QuizMetaDataDTO;
import com.valuelabs.livequiz.model.entity.Question;
import com.valuelabs.livequiz.model.entity.Quiz;
import com.valuelabs.livequiz.repository.QuizRepository;
import com.valuelabs.livequiz.service.question.QuestionService;
import com.valuelabs.livequiz.service.scheduler.UpdateSchedulerService;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.valuelabs.livequiz.exception.ExceptionUtility.throwInvalidResourceDetailsException;
import static com.valuelabs.livequiz.exception.ExceptionUtility.throwResourceExistsException;
import static com.valuelabs.livequiz.utils.InputValidator.validateDTO;
/**
 * Service class for managing Quiz entities, including creation, retrieval, updates and deletion.
 */
@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuestionService questionService;
    private final DisplayQuizService displayQuizService;
    private final UpdateSchedulerService updateSchedulerService;
    private final AuthenticationService authenticationService;
    /**
     * Constructor for QuizService.
     * @param quizRepository         Repository for Quiz entities.
     * @param questionService        Service for managing questions.
     * @param displayQuizService     Service for displaying quiz information.
     * @param updateSchedulerService Service for updating the scheduler information.
     * @param authenticationService is used to fetch the username from security context.
     */
    @Lazy
    @Autowired
    public QuizService(QuizRepository quizRepository, QuestionService questionService, DisplayQuizService displayQuizService, UpdateSchedulerService updateSchedulerService, AuthenticationService authenticationService){
        this.quizRepository = quizRepository;
        this.questionService = questionService;
        this.displayQuizService = displayQuizService;
        this.updateSchedulerService = updateSchedulerService;
        this.authenticationService = authenticationService;
    }
    /**
     * Creates a new quiz based on the provided QuizCreationDTO.
     * @param quizDTO   DTO containing details for quiz creation.
     * @return          The created Quiz entity.
     * @throws ResourceExistsException if an active quiz with the same name already exists.
     * @throws InvalidResourceDetailsException if validation of the provided DTO fails.
     */
    public Quiz createQuiz(QuizCreationDTO quizDTO) {
        validateDTO(quizDTO);
        if(quizRepository.findByQuizNameAndInActive(quizDTO.quizName(),false) == null) {
            Quiz quiz = new Quiz(quizDTO.quizName(), quizDTO.quizType(),authenticationService.getCurrentUserName());
            List<Question> questionList = questionService.addQuestions(quizDTO.questionIds());
            quizDTO.questionList().stream().map(questionService::createQuestion).forEach(questionList::add);
            quiz.setQuestionList(questionList);
//            String quizImagePath = ImageService.storeImageByBase64URL(quizDTO.quizImageBase64URL());
            return saveQuiz(quiz);
        } throwResourceExistsException("Quiz","Active Quiz with quiz name " + quizDTO.quizName() + " is already Present");
        return null;
    }
    /**
     * Saves a quiz entity.
     * @param quiz  The Quiz entity to be saved.
     * @return      The saved Quiz entity.
     * @throws      InvalidResourceDetailsException if the provided quiz is null.
     */
    public Quiz saveQuiz(Quiz quiz){
        if(quiz != null)
            return quizRepository.save(quiz);
        else throwInvalidResourceDetailsException("Quiz","Quiz is not created to be saved!");
        return null;
    }
    /**
     * Updates the metadata of an existing quiz.
     * @param quizId            The ID of the quiz to update.
     * @param quizMetaDataDTO   DTO containing metadata updates.
     * @return The updated Quiz entity.
     * @throws InvalidResourceDetailsException if the quizId is not provided.
     */
    public Quiz updateQuiz(Long quizId, QuizMetaDataDTO quizMetaDataDTO){
        if(quizId != null) {
            Quiz quiz = displayQuizService.getQuizById(quizId, false);
            quiz.setUpdatedBy(authenticationService.getCurrentUserName());
            quiz.setQuizName(quizMetaDataDTO.quizName());
            return saveQuiz(quiz);
        }else throwInvalidResourceDetailsException("Quiz","Quiz Id must be provided!");
        return null;
    }
    /**
     * This method is used to delete the quiz based on the quizId.
     * @param quizId is used to fetch a quiz and soft-delete it if it is present.
     */
    public void deleteQuizById(Long quizId){
        if(quizId != null){
            Quiz quiz = displayQuizService.getQuizById(quizId, false);
            if(!updateSchedulerService.findScheduleByQuiz(quiz)){
                quiz.setInActive(true);
                quiz.setUpdatedBy(authenticationService.getCurrentUserName());
                quizRepository.save(quiz);
            }
            else {
                throwResourceExistsException("Quiz","This Quiz is already Scheduled");
            }
        }
    }
}
