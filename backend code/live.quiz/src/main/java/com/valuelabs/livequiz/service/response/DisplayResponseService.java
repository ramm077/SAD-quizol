package com.valuelabs.livequiz.service.response;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.dto.response.*;
import com.valuelabs.livequiz.model.entity.*;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.repository.ResponseRepository;
import com.valuelabs.livequiz.service.question.DisplayQuestionService;
import com.valuelabs.livequiz.service.scheduler.CreateSchedulerService;
import com.valuelabs.livequiz.service.user.DisplayUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.valuelabs.livequiz.exception.ExceptionUtility.throwInvalidResourceDetailsException;
import static com.valuelabs.livequiz.exception.ExceptionUtility.throwResourceNotFoundException;
/**
 * The DisplayResponseService class provides methods for displaying responses, including individual responses, question-wise responses,
 * and poll responses. It interacts with services related to user display, scheduling, questions, and the response repository.
 */
@Slf4j
@Service
public class DisplayResponseService {
    private final ResponseRepository responseRepository;
    private final DisplayUserService displayUserService;
    private final CreateSchedulerService schedulerService;
    private final DisplayQuestionService displayQuestionService;
    /**
     * Constructs a DisplayResponseService with the specified services and repository.
     *
     * @param responseRepository     Repository for handling responses.
     * @param displayUserService     Service for displaying users.
     * @param schedulerService       used to fetch schedule details or modify them.
     * @param displayQuestionService used to fetch the question details.
     */
    @Autowired
    public DisplayResponseService(ResponseRepository responseRepository, DisplayUserService displayUserService, CreateSchedulerService schedulerService, DisplayQuestionService displayQuestionService) {
        this.responseRepository = responseRepository;
        this.displayUserService = displayUserService;
        this.schedulerService = schedulerService;
        this.displayQuestionService = displayQuestionService;
    }
    /**
     * Displays responses of a user in a specified quiz schedule.
     * @param userId      The ID of the user.
     * @param schedulerId The ID of the quiz schedule.
     * @return DTO containing details of the displayed responses.
     */
    public DisplayResponsesDTO displayResponsesOfUserInSchedule(Long userId,Long schedulerId){
        log.info("Inside DisplayResponseService, displayResponsesOfUserInSchedule method!, validating inputs");
        validateInputForDisplayResponses(userId,schedulerId);
        Scheduler scheduler = schedulerService.getScheduleById(schedulerId);
        Quiz quiz = scheduler.getQuiz();
        User user = displayUserService.getUserByIdAndInActive(userId,false);
        log.debug("User " + user.getUserName() + " Quiz " + quiz.getQuizName() + " scheduler with schedulerId: " + scheduler.getSchedulerId());
        List<QuestionResponseDTO> responses =getResponsesForAllQuestions(userId,schedulerId);
        Integer quizScore = 0;
        if(quiz.getQuizType().equals(QuizType.TEST)) {
            log.info("This quiz is TEST based, hence evaluating quizScore for this quiz " + quiz.getQuizName());
            for (QuestionResponseDTO response : responses) quizScore += response.questionScore();
            log.debug("Successfully evaluated quizScore for this quiz " + quiz.getQuizName());
        }
        else quizScore = -1;
        Question question = displayQuestionService.getQuestionById(responses.get(0).questionId(),false);
        List<Boolean> booleanList = getIsAttemptedAndFinalSubmitOfResponses(user,scheduler,question);
        log.info("Successfully fetched all the responses for the user "+ user.getUserName() + " in quiz " + quiz.getQuizName());
        return new DisplayResponsesDTO(schedulerId,quiz.getQuizName(),quiz.getQuizType(),responses,quizScore,booleanList.get(0),booleanList.get(1));
    }
    /**
     * Retrieves responses for all questions in a specified quiz schedule for a user.
     * @param userId      The ID of the user.
     * @param schedulerId The ID of the quiz schedule.
     * @return List of DTOs containing details of responses for each question.
     */
    public List<QuestionResponseDTO> getResponsesForAllQuestions(Long userId,Long schedulerId){
        log.info("Inside DisplayResponseService, getResponsesForAllQuestions method!");
        User user = displayUserService.getUserByIdAndInActive(userId, false);
        Scheduler scheduler = schedulerService.getScheduleById(schedulerId);
        Quiz quiz = scheduler.getQuiz();
        List<QuestionResponseDTO> responses = new ArrayList<>();
        log.debug("Trying to fetch all the responses of the user "+ user.getUserName() + " in quiz "+quiz.getQuizName());
        for (Question question : quiz.getQuestionList()) {
            getQuestionResponse(question,getResponse(user,scheduler,question),responses);
        }
        return responses;
    }
    /**
     * Validates input details for displaying responses.
     * @param userId The ID of the user.
     * @param schedulerId The ID of the quiz.
     */
    public void validateInputForDisplayResponses(Long userId,Long schedulerId){
        if (userId == null) throwInvalidResourceDetailsException("User","User Id must be provided for displaying responses of a quiz");
        if (schedulerId == null) throwInvalidResourceDetailsException("Scheduler","Scheduler Id must be provided for displaying responses of a User");
    }
    /**
     * Retrieves the response of a user for a specific quiz and question.
     * @param user     The user for whom the response is retrieved.
     * @param scheduler     The quiz for which the response is retrieved.
     * @param question The question for which the response is retrieved.
     * @return The response of the user for the specified quiz and question.
     */
    public Response getResponse(User user,Scheduler scheduler,Question question){
        log.info("Inside DisplayResponseService, getResponse method!, trying to fetch individual responses in a quiz!");
        if(question != null) {
            log.debug("fetching the response for this question "+ question.getQuestionText() + " in quiz " + scheduler.getQuiz().getQuizName() + " for the user "+ user.getUserName());
            Response response = responseRepository.findByUserAndSchedulerAndQuestionAndInActive(user, scheduler, question, false);
            if(response != null) {
                log.info("Successfully fetched the response and returning it");
                return response;
            }else {
                log.error("Response not found for Question" + question.getQuestionText() + ", throwing ResourceNotFoundException!");
                throwResourceNotFoundException("Response","Response not found for Question " + question.getQuestionText());
            }
        }else {
            log.error("Question not found in the quiz " + scheduler.getQuiz().getQuizName() + ", throwing ResourceNotFoundException!");
            throwResourceNotFoundException("Question","Question not found in the quiz " + scheduler.getQuiz().getQuizName());
        }
        return null;
    }
    /**
     * Retrieves information about whether a user has attempted and finalized their response for a specific quiz and question.
     * @param user     The user for whom the information is retrieved.
     * @param scheduler The quiz for which the information is retrieved.
     * @param question  The question for which the information is retrieved.
     * @return List of booleans indicating whether the user has attempted and finalized the response.
     */
    public List<Boolean> getIsAttemptedAndFinalSubmitOfResponses(User user,Scheduler scheduler,Question question){
        log.info("Inside getIsAttemptedAndFinalSubmitOfResponses method!, fetching truth values of isAttempted and finalSubmit of the response");
        Response response = getResponse(user,scheduler,question);
        return List.of(response.getIsAttempted(),response.getFinalSubmit());
    }
    /**
     * Retrieves a QuestionResponseDTO based on the provided question and response.
     * @param question The question for which the response details are retrieved.
     * @param response The response for the specified question.
     * @return QuestionResponseDTO containing question and response details.
     */
    public QuestionResponseDTO getQuestionResponse(Question question, Response response,List<QuestionResponseDTO> responses) {
        log.info("Inside getQuestionResponse method!");
        List<DisplayOptionDTO> displayOptions = new ArrayList<>();
        DisplayResponseDTO displayResponse = getResponseByType(question, response);
        if(question.getQuestionType().equals(QuestionType.SINGLE) || question.getQuestionType().equals(QuestionType.MULTIPLE)) {
            displayOptions = question.getOptionList().stream()
                    .map(option -> new DisplayOptionDTO(option.getOptionId(),
                            option.getOptionText(), option.getIsTrue())).collect(Collectors.toList());
        }
        log.info("returning the response for the question " + question.getQuestionText() + " to display it");
        QuestionResponseDTO questionResponseDTO = new QuestionResponseDTO(question.getQuestionId(),
                question.getQuestionText(),question.getQuestionType(),displayOptions,displayResponse,response.getScoreCount());
        responses.add(questionResponseDTO);
        return questionResponseDTO;
    }
    /**
     * Retrieves a DisplayResponseDTO based on the question type and response details.
     * @param question The question for which the response details are retrieved.
     * @param response The response for the specified question.
     * @return DisplayResponseDTO containing response details based on question type.
     */
    public DisplayResponseDTO getResponseByType(Question question, Response response){
        log.info("Inside getResponseByType method!");
        if(question.getQuestionType().equals(QuestionType.SINGLE) || question.getQuestionType().equals(QuestionType.MULTIPLE)) {
            List<Long> chosenOptionIds = response.getOptionResponse().getChosenOptions().stream().map(Option::getOptionId).collect(Collectors.toList());
            OptionResponseDTO optionResponse = new OptionResponseDTO(response.getOptionResponse().getOptionResponseId(),chosenOptionIds);
            log.debug("responseId: "+response.getResponseId() + " optionResponseId: "+optionResponse.optionResponseId());
            log.info("Successfully fetched all the options, returning them to display!");
            return new DisplayResponseDTO(response.getResponseId(),null, optionResponse);
        }
        else{
            String answerText = response.getTextResponse().getAnswerText();
            TextResponseDTO textResponse = new TextResponseDTO(response.getTextResponse().getTextResponseId(),answerText);
            log.debug("responseId: "+response.getResponseId() + " textResponseId: "+textResponse.textResponseId());
            log.info("Successfully fetched the text response, returning them to display!");
            return new DisplayResponseDTO(response.getResponseId(),textResponse,null);
        }
    }
    /**
     * Retrieves a response based on the provided response ID.
     * @param responseId The ID of the response to retrieve.
     * @return The retrieved response.
     * @throws ResourceNotFoundException      If the response is not found.
     * @throws InvalidResourceDetailsException If no response ID is provided.
     */
    public Response getResponseById(Long responseId){
        log.info("Inside getResponseByType method!");
        if(responseId != null){
            log.debug("responseId: "+responseId);
            return responseRepository.findByResponseIdAndInActive(responseId, false).orElseThrow(() -> {log.error("Response not found with Id: " + responseId + ", throwing ResourceNotFoundException!"); throwResourceNotFoundException("Resource","Response not found with Id: " + responseId);return null;});
        }log.error("Response Id must be provided!, throwing InvalidResourceDetailsException!");
        throwInvalidResourceDetailsException("Response","Response Id must be provided!");
        return null;
    }
    /**
     * Displays all responses of a poll for a specified quiz schedule.
     * @param schedulerId The ID of the quiz schedule.
     * @return Object containing counts of chosen options for the poll.
     */
    public Object displayAllResponsesOfPoll(Long schedulerId){
        log.info("Inside displayAllResponsesOfPoll method!");
        Scheduler scheduler = schedulerService.getScheduleById(schedulerId);
        List<Response> responseList = responseRepository.findAllBySchedulerAndInActive(scheduler,false);
        SortedMap<Long,Integer> optionCounts = new TreeMap<>();
        Quiz quiz = scheduler.getQuiz();
        if(quiz.getQuestionList() != null && !quiz.getQuestionList().isEmpty()) {
            if (scheduler.getQuiz().getQuizType().equals(QuizType.POLL)) {
                log.debug("Poll " + quiz.getQuizName());
                log.info("Fetching the count of responses for each option by the responders!");
                for (Response response : responseList) {
                    Option option = response.getOptionResponse().getChosenOptions().get(0);
                    if(option != null){
                        optionCounts.put(option.getOptionId(), optionCounts.getOrDefault(option.getOptionId(), 0) + 1);
                    }
                }
            }
        }
        assert quiz.getQuestionList() != null;
        List<OptionDTO> optionList = new ArrayList<>();
        Integer totalResponses = 0;
        for(Option option : quiz.getQuestionList().get(0).getOptionList()) {
            OptionDTO optionDTO = new OptionDTO(option.getOptionId(), option.getOptionText(), optionCounts.get(option.getOptionId()));
            optionList.add(optionDTO);
            if(optionCounts.get(option.getOptionId()) != null) {
                totalResponses += optionCounts.get(option.getOptionId());
            }
        }
        log.info("Successfully fetched all the responses of the Poll " + quiz.getQuizName() + " of the schedule with Id: "+ scheduler.getSchedulerId() + ", returning them to display!");
        return new PollResponsesDTO(quiz.getQuizName(),quiz.getQuestionList().get(0).getQuestionId(),quiz.getQuestionList().get(0).getQuestionText(),optionList,totalResponses);
    }
}
