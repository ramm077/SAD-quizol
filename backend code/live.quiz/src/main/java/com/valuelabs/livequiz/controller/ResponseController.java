package com.valuelabs.livequiz.controller;

import com.valuelabs.livequiz.model.dto.request.CaptureResponsesDTO;
import com.valuelabs.livequiz.model.dto.request.UpdateResponsesDTO;
import com.valuelabs.livequiz.model.dto.response.DisplayResponsesDTO;
import com.valuelabs.livequiz.model.dto.response.ErrorStatusDTO;
import com.valuelabs.livequiz.model.entity.Response;
import com.valuelabs.livequiz.service.response.DeleteResponseService;
import com.valuelabs.livequiz.service.response.DisplayResponseService;
import com.valuelabs.livequiz.service.response.ResponseCaptureService;
import com.valuelabs.livequiz.service.response.UpdateResponseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.channels.ScatteringByteChannel;
import java.util.List;
import java.util.SortedMap;

/**
 * Controller responsible for handling user responses in quizzes.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/response")
@Slf4j
public class ResponseController {
    private final ResponseCaptureService responseCaptureService;
    private final DisplayResponseService displayResponseService;
    private final UpdateResponseService updateResponseService;
    private final DeleteResponseService deleteResponseService;
    /**
     * Constructor to inject dependencies.
     */
    @Autowired
    public ResponseController(ResponseCaptureService responseCaptureService, DisplayResponseService displayResponseService, UpdateResponseService updateResponseService, DeleteResponseService deleteResponseService) {
        this.responseCaptureService = responseCaptureService;
        this.displayResponseService = displayResponseService;
        this.updateResponseService = updateResponseService;
        this.deleteResponseService = deleteResponseService;
    }
    /**
     * Endpoint to capture user responses in a quiz.
     * @param userId      ID of the user capturing responses.
     * @param schedulerId      ID of the quiz for which responses are captured.
     * @param responseDTOS Set of response DTOs containing user responses.
     * @return ResponseEntity with the captured responses or an error status.
     */
    @PostMapping("/capture")
    public ResponseEntity<?> responseCaptureOfUserInQuiz(@RequestParam Long userId, @RequestParam Long schedulerId, @RequestBody CaptureResponsesDTO responseDTOS){
        log.info("Inside ResponseController, responseCaptureOfUserInQuiz method!");
        List<Response> responseList = responseCaptureService.captureResponsesOfPersonInQuiz(userId, schedulerId, responseDTOS);
        log.debug("Successfully Captured Responses of the User with id: "+ userId + " for the Schedule with id: " + schedulerId);
        return new ResponseEntity<>(responseList, HttpStatus.CREATED);
    }
    /**
     * Endpoint to display user responses in a quiz.
     * @param userId ID of the user whose responses are to be displayed.
     * @param schedulerId ID of the quiz for which responses are to be displayed.
     * @return ResponseEntity with the displayed responses or an error status.
     */
    @GetMapping("/display")
    public ResponseEntity<?> displayResponsesOfUserInQuiz(@RequestParam Long userId, @RequestParam Long schedulerId){
            log.info("Inside ResponseController, displayResponsesOfUserInQuiz method!");
            DisplayResponsesDTO responses = displayResponseService.displayResponsesOfUserInSchedule(userId, schedulerId);
            log.debug("Successfully found all the responses of user with id: "+ userId+" for the quiz with id: "+ schedulerId);
            return new ResponseEntity<>(responses, HttpStatus.FOUND);
    }
    /**
     * Endpoint to update user responses in a quiz.
     * @param userId      ID of the user updating responses.
     * @param schedulerId      ID of the quiz for which responses are updated.
     * @param responseDTO List of response DTOs containing updated responses.
     * @return ResponseEntity with the updated responses or an error status.
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateResponsesOfUserInQuiz(@RequestParam Long userId, @RequestParam Long schedulerId, @RequestBody UpdateResponsesDTO responseDTO){
        log.info("Inside ResponseController, updateResponsesOfUserInQuiz method!");
        List<Response> responseList = updateResponseService.updateResponsesOfPersonInQuiz(userId, schedulerId, responseDTO);
        log.debug("Successfully updated all the responses of user with id: "+ userId+" for the schedule with id: "+ schedulerId);
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
    /**
     * Endpoint to delete user responses in a quiz.
     * @param userId      ID of the user updating responses.
     * @param schedulerId      ID of the quiz for which responses are updated.
     * @return ResponseEntity with the deleted responses or an error status.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteResponsesOfUserInQuiz(@RequestParam Long userId,@RequestParam Long schedulerId){
        try{
            deleteResponseService.deleteResponsesOfUserInQuiz(userId,schedulerId);
            return new ResponseEntity<>("Responses Deleted Successfully",HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(new ErrorStatusDTO(e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * Endpoint to get all user responses of a poll.
     * @param schedulerId      ID of the poll for which responses are displayed.
     * @return ResponseEntity with the displaying responses or an error status.
     */
    @GetMapping("/all")
    public ResponseEntity<?> displayAllResponsesOfPoll(@RequestParam Long schedulerId){
        log.info("Inside ResponseController, displayResponsesOfUserInQuiz method!");
        Object responses = displayResponseService.displayAllResponsesOfPoll(schedulerId);
        log.debug("Successfully found all the responses of schedule with id: "+ schedulerId);
        return new ResponseEntity<>(responses, HttpStatus.FOUND);
    }

}
