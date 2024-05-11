package com.valuelabs.livequiz.service.response;

import com.valuelabs.livequiz.model.dto.request.CaptureResponseDTO;
import com.valuelabs.livequiz.model.dto.request.CaptureResponsesDTO;
import com.valuelabs.livequiz.model.entity.*;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.repository.ResponseRepository;
import com.valuelabs.livequiz.service.option.DisplayOptionService;
import com.valuelabs.livequiz.service.optionresponse.OptionResponseService;
import com.valuelabs.livequiz.service.question.DisplayQuestionService;
import com.valuelabs.livequiz.service.scheduler.CreateSchedulerService;
import com.valuelabs.livequiz.service.textresponse.TextResponseService;
import com.valuelabs.livequiz.service.user.DisplayUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.valuelabs.livequiz.exception.ExceptionUtility.*;
import static com.valuelabs.livequiz.utils.InputValidator.validateDTO;

/**
 * Service class for capturing and managing responses.
 * @Service
 */
@Slf4j
@Service
public class ResponseCaptureService {
    private final DisplayQuestionService displayQuestionService;
    private final DisplayOptionService displayOptionService;
    private final DisplayUserService displayUserService;
    private final ResponseRepository responseRepository;
    private final CreateSchedulerService schedulerService;
    private final OptionResponseService optionResponseService;
    private final TextResponseService textResponseService;
    /**
     * Constructs a ResponseCaptureService with the specified services and repository.
     * @param displayQuestionService Service for displaying questions.
     * @param displayOptionService   Service for displaying options.
     * @param displayUserService     Service for displaying users.
     * @param responseRepository     Repository for handling responses.
     * @param optionResponseService  for creating, saving option responses.
     * @param textResponseService    for creating, saving text responses.
     */
    @Autowired
    public ResponseCaptureService(DisplayQuestionService displayQuestionService, DisplayOptionService displayOptionService, DisplayUserService displayUserService, ResponseRepository responseRepository, CreateSchedulerService schedulerService, OptionResponseService optionResponseService, TextResponseService textResponseService) {
        this.displayQuestionService = displayQuestionService;
        this.displayOptionService = displayOptionService;
        this.displayUserService = displayUserService;
        this.responseRepository = responseRepository;
        this.schedulerService = schedulerService;
        this.optionResponseService = optionResponseService;
        this.textResponseService = textResponseService;
    }
    /**
     * Captures responses of a person in a specific quiz.
     * @param userId       The ID of the user.
     * @param schedulerId       The ID of the quiz.
     * @param responseDTO List of data transfer objects containing response details.
     * @return List of captured responses.
     */
    public List<Response> captureResponsesOfPersonInQuiz(Long userId, Long schedulerId, CaptureResponsesDTO responseDTO) {
        log.info("Inside ResponseCaptureService, captureResponsesOfPersonInQuiz method!");
        User user = displayUserService.getUserByIdAndInActive(userId, false);
        Scheduler scheduler = schedulerService.getScheduleById(schedulerId);
        log.debug("User " + user.getUserName() + " SchedulerId: " + scheduler.getSchedulerId());
        List<Response> responseList = new ArrayList<>();
        log.info("Capturing the responses for the User " + user.getUserName() + " in quiz "+scheduler.getQuiz().getQuizName());
        for (CaptureResponseDTO response : responseDTO.responseList()) {
            saveResponseAndAddToList(responseList,
                    captureResponseForQuestion(user, scheduler, response, responseDTO.finalSubmit()));
        }
        log.info("Successfully captured the responses for the User " + user.getUserName() + " in quiz "+scheduler.getQuiz().getQuizName());
        return responseList;
    }
    /**
     * This method is used to save a certain response
     * @param response object is provided to save the response using ResponseRepository.
     * @return response or throw InvalidResourceDetails Exception if response is not provided!.
     */
    public Response saveResponse(Response response){
        if(response != null){
            log.info("Saving the response with id " + response.getResponseId());
            return responseRepository.save(response);
        }
        else throwInvalidResourceDetailsException("Response","Response must be provided!");
        return null;
    }
    /**
     * Captures response of a question in a specific quiz for a person .
     * @param user       The user for whom the response is captured.
     * @param scheduler       The quiz for which the response is captured.
     * @param responseDTO The data transfer object containing response details..
     * @return Captured response.
     */
    public Response captureResponseForQuestion(User user,Scheduler scheduler,CaptureResponseDTO responseDTO,Boolean finalSubmit){
        log.info("Inside captureResponseForQuestion method!, validating CaptureResponseDTO");
        validateDTO(responseDTO);
        Question question = displayQuestionService.getQuestionById(responseDTO.questionId(), false);
        Response response = responseRepository.findByUserAndSchedulerAndQuestionAndInActive(user, scheduler, question, false);
        Quiz quiz = scheduler.getQuiz();
        log.debug("User " + user.getUserName() + " schedulerId: " + scheduler.getSchedulerId() + " Quiz " + quiz.getQuizName());
        if (response == null) {
            response = createResponse(user, scheduler, question);
            if(quiz.getQuizType().equals(QuizType.TEST)) {
                validateOptionsAndSetScore(responseDTO.questionId(), response, chosenOptions(responseDTO.questionId(),responseDTO.chosenOptions()), correctOptions(responseDTO.questionId()));
            }
            else{
                if(question.getQuestionType().equals(QuestionType.SINGLE) || question.getQuestionType().equals(QuestionType.MULTIPLE)){
                    response.setOptionResponse(optionResponseService.createOptionResponse(chosenOptions(responseDTO.questionId(), responseDTO.chosenOptions())));
                }
                else response.setTextResponse(textResponseService.createTextResponse(responseDTO.answerText()));
            }
            response.setQuestion(question);
            response.setFinalSubmit(finalSubmit);
        } else {
            throwResourceExistsException("Response", "The User with UserId: " + user.getUserId() + " " +
                    "has already given response for question with questionId: " + responseDTO.questionId() +
                    " in Schedule with scheduleId: " + scheduler.getSchedulerId() + ". This user can only update his responses for this quiz");
        }
        return response;
    }
    /**
     * Creates a new response based on the provided user, quiz, and response details.
     * @param user         The user for whom the response is captured.
     * @param scheduler         The quiz for which the response is captured.
     * @param question  The data transfer object containing response details.
     * @return The created Response.
     */
    public Response createResponse(User user,Scheduler scheduler,Question question){
        log.info("response created using user, scheduler and question");
        log.debug("userId: {}, schedulerId: {}, questionId: {}",user.getUserId(),scheduler.getSchedulerId(),question.getQuestionId());
        return new Response(user,scheduler,question);
    }
    /**
     * Retrieves the correct options for a specific question.
     * @param questionId The ID of the question.
     * @return List of correct options for the specified question.
     */
    public List<Option> correctOptions(Long questionId){
        log.info("correct options are fetched for the question with id: "+questionId+" returning them!");
        log.debug("questionId: {}",questionId);
        return displayQuestionService.getQuestionById(questionId,false).getOptionList()
                    .stream().filter(Option::getIsTrue).collect(Collectors.toList());
    }
    /**
     * Retrieves a list of chosen options based on the provided response details.
     * @param chosenOptions The data transfer object containing response details.
     * @return List of chosen options.
     */
    public List<Option> chosenOptions(Long questionId,List<Long> chosenOptions) {
        if(chosenOptions != null && !chosenOptions.isEmpty()) {
            log.info("chosen options are fetched for the question with id: "+questionId+" returning them!");
            log.debug("questionId: {}",questionId);
            return chosenOptions.stream().map(optionId ->
                    displayOptionService.getOptionById(optionId,false)).collect(Collectors.toList());
        } log.warn("no chosen Options 1for this questionId: {}",questionId);
        return new ArrayList<>();
    }
    /**     * Validates options, sets the score, and updates the response based on the provided details.
     * @param questionId      The ID of the question.
     * @param response        The response to be updated.
     * @param chosenOptionList List of chosen options.
     * @param correctOptionList List of correct options.
     */
    public Response validateOptionsAndSetScore(Long questionId, Response response, List<Option> chosenOptionList, List<Option> correctOptionList) {
        log.info("Setting the OptionResponse for the response with id: "+response.getResponseId());
        if(response.getOptionResponse() == null) response.setOptionResponse(optionResponseService.createOptionResponse(chosenOptionList));
        log.info("checking whether all the chosen options are correct or not!");
        boolean isAllChosenOptionsAreCorrect = chosenOptionList.stream().allMatch(Option::getIsTrue);
        response.setScoreCount(isAllChosenOptionsAreCorrect && correctOptionList.size() == chosenOptionList.size() ? 1 : 0);
        if (chosenOptionList.stream().anyMatch(option -> !correctOptionList.contains(option))) response.setScoreCount(0);
        log.debug("scoreCount for the questionId : {} is {}",questionId,response.getScoreCount());
        log.info((response.getScoreCount() == 0) ? "the response for this question with id: "+questionId+" is incorrect!, so score is 0" : "the response for this question with id: "+questionId+" is correct!, so score is 1");
        return response;
    }
    /**
     * Saves the response and adds it to the provided list.
     * @param responseList List of responses.
     * @param response     The response to be saved.
     */
    public void saveResponseAndAddToList(List<Response> responseList, Response response) {
        log.info("Saving the response and adding it to the responseList");
        response.setCreatedBy(response.getUser().getUserName());
        response.setIsAttempted(true);
        responseRepository.save(response);
        if(responseList != null) {
            responseList.add(response);
            log.debug("responseId: "+response.getResponseId() + " responseList size: "+responseList.size());
        }
    }
}
