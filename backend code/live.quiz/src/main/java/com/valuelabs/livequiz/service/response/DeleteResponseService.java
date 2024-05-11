package com.valuelabs.livequiz.service.response;

import com.valuelabs.livequiz.model.entity.*;
import com.valuelabs.livequiz.service.scheduler.CreateSchedulerService;
import com.valuelabs.livequiz.service.user.DisplayUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * The DeleteResponseService class provides methods for deleting responses of a user in a quiz schedule.
 * It interacts with services related to user display, scheduling, response display, and response capture.
 */
@Slf4j
@Service
public class DeleteResponseService {
    private final DisplayUserService userService;
    private final CreateSchedulerService schedulerService;
    private final DisplayResponseService displayResponseService;
    private final ResponseCaptureService responseCaptureService;
    @Autowired
    public DeleteResponseService(DisplayUserService userService, CreateSchedulerService schedulerService, DisplayResponseService displayResponseService, ResponseCaptureService responseCaptureService) {
        this.userService = userService;
        this.schedulerService = schedulerService;
        this.displayResponseService = displayResponseService;
        this.responseCaptureService = responseCaptureService;
    }
    /**
     * Deletes responses of a user in a specified quiz schedule.
     * @param userId      The ID of the user.
     * @param schedulerId The ID of the quiz schedule.
     */
    public void deleteResponsesOfUserInQuiz(Long userId,Long schedulerId){
        log.info("Inside DeleteResponseService, deleteResponsesOfUserInQuiz method!");
        User user = userService.getUserByIdAndInActive(userId,false);
        log.debug("User " + user.getUserName());
        Scheduler scheduler = schedulerService.getScheduleById(schedulerId);
        log.debug("Scheduler with schedulerId: " + scheduler.getSchedulerId());
        Quiz quiz = scheduler.getQuiz();
        if(quiz != null) {
            log.debug("Quiz " + quiz.getQuizName());
            int n = quiz.getQuestionList().size();
            log.info("Successfully fetched user, scheduler and quiz details,trying to delete responses!");
            for (int i = 0; i < n; i++) {
                Question question = quiz.getQuestionList().get(i);
                Response response = displayResponseService.getResponse(user, scheduler, question);
                response.setInActive(true);
                response.setUpdatedBy(user.getUserName());
                responseCaptureService.saveResponse(response);
                log.info("Successfully deleted the responses!");
            }
        }
    }
}
