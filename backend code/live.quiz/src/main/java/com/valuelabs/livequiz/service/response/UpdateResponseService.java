package com.valuelabs.livequiz.service.response;

import com.valuelabs.livequiz.model.dto.request.UpdateOptionResponseDTO;
import com.valuelabs.livequiz.model.dto.request.UpdateResponsesDTO;
import com.valuelabs.livequiz.model.dto.request.UpdateTextResponseDTO;
import com.valuelabs.livequiz.model.entity.*;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.service.optionresponse.OptionResponseService;
import com.valuelabs.livequiz.service.scheduler.CreateSchedulerService;
import com.valuelabs.livequiz.service.textresponse.TextResponseService;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.valuelabs.livequiz.utils.InputValidator.validateDTO;

/**
 * The UpdateResponseService class provides methods for updating responses in a quiz, including text responses and option responses.
 * It interacts with services related to scheduling, text responses, option responses, display responses, and response capture.
 */
@Lazy
@Service
@Slf4j
public class UpdateResponseService {
    private final CreateSchedulerService schedulerService;
    private final TextResponseService textResponseService;
    private final OptionResponseService optionResponseService;
    private final DisplayResponseService displayResponseService;
    private final ResponseCaptureService responseCaptureService;
    private final AuthenticationService authenticationService;
    @Autowired
    public UpdateResponseService(CreateSchedulerService schedulerService, TextResponseService textResponseService, OptionResponseService optionResponseService, DisplayResponseService displayResponseService, ResponseCaptureService responseCaptureService, AuthenticationService authenticationService) {
        this.schedulerService = schedulerService;
        this.textResponseService = textResponseService;
        this.optionResponseService = optionResponseService;
        this.displayResponseService = displayResponseService;
        this.responseCaptureService = responseCaptureService;
        this.authenticationService = authenticationService;
    }
    /**
     * Updates responses of a person in a quiz based on the provided user ID, scheduler ID, and UpdateResponsesDTO.
     * @param userId      The ID of the user whose responses are to be updated.
     * @param schedulerId The ID of the scheduler associated with the quiz.
     * @param responseDTO The Data Transfer Object (DTO) containing updated responses.
     * @return A list of updated responses.
     */
    public List<Response> updateResponsesOfPersonInQuiz(Long userId, Long schedulerId, UpdateResponsesDTO responseDTO) {
        log.info("Inside UpdateResponseService, updateResponsesOfPersonInQuiz method!");
        Scheduler scheduler = schedulerService.getScheduleById(schedulerId);
        Quiz quiz = scheduler.getQuiz();
        log.debug("schedulerId: {}, quizId: {}",scheduler.getSchedulerId(),quiz.getQuizId());
        List<Response> responseList = new ArrayList<>();
        if(quiz.getQuizType().equals(QuizType.OPEN_ENDED)) {
            log.info("updating textResponses for the Open_ended quiz!");
            for (UpdateTextResponseDTO textResponse : responseDTO.textResponses()) {
                updateTextResponses(quiz,textResponse,responseDTO.finalSubmit(),responseList);
            }
        }log.info("Updating all the optionResponses!");
        for(UpdateOptionResponseDTO optionResponse : responseDTO.optionResponses()){
            updateOptionResponses(quiz,optionResponse,responseDTO.finalSubmit(),responseList);
        }log.debug("responseList size: {}",responseList.size());
        log.info("Successfully updated all the responses returning the entire list of updated responses!");
        return responseList;
    }
    /**
     * Updates text responses in a quiz based on the provided quiz, text response DTO, and final submit flag.
     * @param quiz       The quiz associated with the responses.
     * @param responseDTO The Data Transfer Object (DTO) containing updated text response details.
     * @param finalSubmit A flag indicating whether the update is a final submission.
     * @return The updated response.
     */
    public Response updateTextResponses(Quiz quiz,UpdateTextResponseDTO responseDTO,Boolean finalSubmit,List<Response> responseList){
        log.info("Inside UpdateResponseService, updateTextResponses method!, validating UpdateTextResponseDTO!");
        validateDTO(responseDTO);
        TextResponse textResponse1 = textResponseService.getTextResponseById(responseDTO.textResponseId());
        textResponse1.setAnswerText(responseDTO.answerText());
        textResponseService.saveTextResponse(textResponse1);
        Response response = displayResponseService.getResponseById(responseDTO.responseId());
        response.setTextResponse(textResponse1);
        response.setUpdatedBy(authenticationService.getCurrentUserName());
        response.setFinalSubmit(finalSubmit);
        responseCaptureService.saveResponse(response);
        responseList.add(response);
        log.debug("responseList Size: {}, updated answerText: {}, updated ResponseId: {}, updated textResponseId: {}",responseList.size(),responseDTO.answerText(),responseDTO.responseId(),responseDTO.textResponseId());
        log.info("Successfully updated the textResponse, returning it after adding the response to responseList!");
        return response;
    }
    /**
     * Updates option responses in a quiz based on the provided quiz, option response DTO, and final submit flag.
     * @param quiz       The quiz associated with the responses.
     * @param responseDTO The Data Transfer Object (DTO) containing updated option response details.
     * @param finalSubmit A flag indicating whether the update is a final submission.
     * @return The updated response.
     */
    public Response updateOptionResponses(Quiz quiz,UpdateOptionResponseDTO responseDTO,Boolean finalSubmit,List<Response> responseList){
        log.info("Inside UpdateResponseService, updateOptionResponses method!, validating UpdateOptionResponseDTO!");
        validateDTO(responseDTO);
        OptionResponse optionResponse = optionResponseService.getOptionResponseById(responseDTO.optionResponseId());
        List<Option> chosenOptions = responseCaptureService.chosenOptions(responseDTO.questionId(), responseDTO.chosenOptions());
        optionResponse.setChosenOptions(chosenOptions);
        optionResponseService.saveOptionResponse(optionResponse);
        Response response = displayResponseService.getResponseById(responseDTO.responseId());
        response.setOptionResponse(optionResponse);
        response.setUpdatedBy(authenticationService.getCurrentUserName());
        if(quiz.getQuizType().equals(QuizType.TEST)) {
            List<Option> correctOptions = responseCaptureService.correctOptions(responseDTO.questionId());
            responseCaptureService.validateOptionsAndSetScore(responseDTO.questionId(), response,chosenOptions,correctOptions);
        }
        response.setFinalSubmit(finalSubmit);
        responseCaptureService.saveResponse(response);
        responseList.add(response);
        log.debug("responseList Size: {}, updated Options: {}, updated ResponseId: {}, updated optionResponseID: {}",responseList.size(),chosenOptions.size(),responseDTO.responseId(),responseDTO.optionResponseId());
        log.info("Successfully updated the optionResponse, returning it after adding the response to responseList!");
        return response;
    }
}