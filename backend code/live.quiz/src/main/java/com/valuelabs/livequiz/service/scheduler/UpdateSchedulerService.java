package com.valuelabs.livequiz.service.scheduler;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.dto.request.UpdateScheduleRequestDTO;
import com.valuelabs.livequiz.model.entity.Quiz;
import com.valuelabs.livequiz.model.entity.Scheduler;
import com.valuelabs.livequiz.model.entity.User;
import com.valuelabs.livequiz.repository.SchedulerRepository;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import com.valuelabs.livequiz.service.user.DisplayUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static com.valuelabs.livequiz.exception.ExceptionUtility.throwInvalidResourceDetailsException;
import static com.valuelabs.livequiz.exception.ExceptionUtility.throwResourceNotFoundException;

/**
 * This class is used to update schedules and delete scheduler entity.
 */
@Lazy
@Service
@Slf4j
public class UpdateSchedulerService {
    private final ScheduleReminderService scheduleReminderService;
    private final SchedulerRepository schedulerRepository;
    private final DisplayUserService displayUserService;
    private final AuthenticationService authenticationService;
    /**
     * Constructor Injection of all the classes which are required in this class.
     * @param scheduleReminderService is used to send remainder emails for persons.
     * @param schedulerRepository is used to perform database operations of Scheduler entity.
     * @param displayUserService is used to fetch details of the user.
     * @param authenticationService is used to retrieve the user details from security context.
     */
    @Lazy
    @Autowired
    public UpdateSchedulerService(ScheduleReminderService scheduleReminderService, SchedulerRepository schedulerRepository, DisplayUserService displayUserService, AuthenticationService authenticationService) {
        this.scheduleReminderService = scheduleReminderService;
        this.schedulerRepository = schedulerRepository;
        this.displayUserService = displayUserService;
        this.authenticationService = authenticationService;
    }
    /**
     * Updates an existing schedule with the specified details, including a new list of associated persons,
     * start and end times, and a reason for the update.
     * @param schedulerId The unique identifier of the schedule to be updated.
     * @param updateScheduleRequestDTO The Data Transfer Object (DTO) containing schedule update details.
     * @return Boolean indicating the success of the update operation.
     * @throws ResourceNotFoundException if the specified schedule is not found.
     */
    public Boolean updateScheduleWithReason(Long schedulerId, UpdateScheduleRequestDTO updateScheduleRequestDTO){
        log.info("Updating the schedule with id"+schedulerId);
        Scheduler scheduler = schedulerRepository.findBySchedulerIdAndInActive(schedulerId,false)
                .orElseThrow(() -> {throwResourceNotFoundException("Scheduler","Schedule not found with id: " + schedulerId); return null;});
        List<User> existingUserList = scheduler.getUserList();
        List<User> addUserList = displayUserService.getUserListByIdsAndInactive(updateScheduleRequestDTO.addPersonIdList(),false);
        List<User> deleteUserList = displayUserService.getUserListByIdsAndInactive(updateScheduleRequestDTO.deletePersonIdList(),false);
        existingUserList.addAll(addUserList);
        existingUserList.removeAll(deleteUserList);
        scheduler.setUserList(existingUserList);
        scheduler.setReason(updateScheduleRequestDTO.reason());
        scheduler.setStartTime(Timestamp.valueOf(updateScheduleRequestDTO.startTime()));
        scheduler.setEndTime(Timestamp.valueOf(updateScheduleRequestDTO.endTime()));
        scheduler.setUpdatedBy(authenticationService.getCurrentUserName());//scheduler.setUpdatedBy(userService.getCurrentPersonName());
        try {
            schedulerRepository.save(scheduler);
            scheduleReminderService.cancelReminderTask(schedulerId);
            scheduleReminderService.scheduleSendUpdateScheduler(existingUserList,addUserList,deleteUserList,scheduler);
            scheduleReminderService.scheduleReminders(scheduler);
            log.debug("Schedule updated successfully");
            return true;
        } catch (Exception e) {
            log.error("Updating the schedule caused error:"+e);
            throw new InvalidResourceDetailsException("Scheduler","An error occurred while updating the user with new personaList"+e);
        }
    }
    /**
     * Marks an existing schedule as inactive (soft delete) with the specified reason,
     * cancels associated reminders, and triggers emailId notifications for schedule cancellation.
     * @param schedulerId The unique identifier of the schedule to be deleted.
     * @param reason The reason for deleting/cancelling the schedule.
     * @throws ResourceNotFoundException if the specified schedule is not found.
     * @throws InvalidResourceDetailsException if an error occurs during the deletion process.
     */
    public Boolean deleteScheduleById(Long schedulerId,String reason){
        log.info("Deleting the schedule with id"+schedulerId);
        Scheduler existingSchedule = schedulerRepository.findBySchedulerIdAndInActive(schedulerId,false)
                .orElseThrow(() -> {throwResourceNotFoundException("Scheduler","Schedule not found with id: " + schedulerId); return null;});
        existingSchedule.setInActive(true);
        existingSchedule.setReason(reason);
        existingSchedule.setUpdatedBy(authenticationService.getCurrentUserName());//existingSchedule.setUpdatedBy(userService.getCurrentPersonName());
        schedulerRepository.save(existingSchedule);
        log.debug("Schedule deleted successfully");
        scheduleReminderService.cancelReminderTask(schedulerId);
        scheduleReminderService.scheduleSendCancelScheduler(existingSchedule, reason);
        return true;
    }
    /**
     * This method is to check whether a quiz has been scheduled or not.
     * @param quiz this the quiz which is used for checking whether it's scheduled.
     * @return the truth value of existence of a schedule for a quiz.
     */
    public Boolean findScheduleByQuiz(Quiz quiz){
        if(quiz != null){
            return !schedulerRepository.findAllByQuizAndInActive(quiz, false).isEmpty();
        }
        throwInvalidResourceDetailsException("Quiz","Quiz must be provided!");
        return null;
    }
}
