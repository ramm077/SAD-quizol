package com.valuelabs.livequiz.service.scheduler;
import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.dto.request.DraftScheduleDTO;
import com.valuelabs.livequiz.model.entity.Quiz;
import com.valuelabs.livequiz.model.entity.Scheduler;
import com.valuelabs.livequiz.model.entity.User;
import com.valuelabs.livequiz.repository.SchedulerRepository;
import com.valuelabs.livequiz.service.quiz.DisplayQuizService;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import com.valuelabs.livequiz.service.user.DisplayUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.valuelabs.livequiz.utils.InputValidator.validateDTO;
/**
 * The SchedulerExtensionService class provides methods for managing scheduler extensions, including creating default
 * schedules, updating schedules with time, updating schedules with persons, and updating schedules with new time and persons.
 */
@Service
public class SchedulerExtensionService {
    private final DisplayQuizService quizService;
    private final SchedulerRepository schedulerRepository;
    private final DisplayUserService userService;
    private final AuthenticationService authenticationService;
    @Autowired
    public SchedulerExtensionService(DisplayQuizService quizService, SchedulerRepository schedulerRepository, DisplayUserService userService, AuthenticationService authenticationService) {
        this.quizService = quizService;
        this.schedulerRepository = schedulerRepository;
        this.userService = userService;
        this.authenticationService = authenticationService;
    }
    /**
     * Creates a default schedule based on the provided DraftScheduleDTO.
     * @param draftScheduleDTO The Data Transfer Object (DTO) containing draft schedule details.
     * @return The newly created scheduler.
     * @throws InvalidResourceDetailsException If validation fails or the associated quiz is not found.
     */
    public Scheduler createDefaultSchedule(DraftScheduleDTO draftScheduleDTO){
        validateDTO(draftScheduleDTO);
        Quiz quiz = quizService.getQuizById(draftScheduleDTO.quizId(),false);
        Scheduler scheduler = new Scheduler();
        scheduler.setStartTime(Timestamp.valueOf(draftScheduleDTO.startTime()));
        scheduler.setEndTime(Timestamp.valueOf(draftScheduleDTO.endTime()));
        scheduler.setCreatedBy(authenticationService.getCurrentUserName());//scheduler.setCreatedBy(userService.getCurrentPersonName());
        scheduler.setQuiz(quiz);
        scheduler = schedulerRepository.save(scheduler);
        return scheduler;
    }
    /**
     * Updates the schedule with the specified ID by modifying its start and end times.
     * @param id           The ID of the schedule to be updated.
     * @param newStartTime The new start time for the schedule.
     * @param newEndTime   The new end time for the schedule.
     * @return True if the update is successful, false otherwise.
     * @throws ResourceNotFoundException      If the schedule is not found.
     * @throws InvalidResourceDetailsException If an error occurs during the update.
     */
    public Boolean updateScheduleWithTime(Long id, String newStartTime, String newEndTime){
        Scheduler scheduler = schedulerRepository.findBySchedulerIdAndInActive(id,false)
                .orElseThrow(() -> new ResourceNotFoundException("Scheduler","Schedule not found with id: " + id));
        scheduler.setStartTime(Timestamp.valueOf(newStartTime));
        scheduler.setEndTime(Timestamp.valueOf(newEndTime));
        scheduler.setUpdatedBy(authenticationService.getCurrentUserName());//scheduler.setUpdatedBy(userService.getCurrentPersonName());
        try {
            schedulerRepository.save(scheduler);
            return true;
        } catch (Exception e) {
            throw new InvalidResourceDetailsException("Scheduler","An error occurred while updating the Schedules with time: "+e);
        }
    }
    /**
     * Updates the schedule with the specified ID by modifying its associated user list.
     * @param id             The ID of the schedule to be updated.
     * @param newPersonIdList The list of IDs of users to be associated with the schedule.
     * @return True if the update is successful, false otherwise.
     * @throws ResourceNotFoundException      If the schedule is not found.
     * @throws InvalidResourceDetailsException If an error occurs during the update.
     */
    public Boolean updateScheduleWithPerson(Long id, List<Long> newPersonIdList){
        Scheduler scheduler = schedulerRepository.findBySchedulerIdAndInActive(id,false)
                .orElseThrow(() -> new ResourceNotFoundException("Scheduler","Schedule not found with id: " + id));
        List<User> newUserList = new ArrayList<>();
        for(Long personId:newPersonIdList){
            User user = userService.getUserByIdAndInActive(personId,false);
            newUserList.add(user);
        }
        scheduler.setUserList(newUserList);
        scheduler.setUpdatedBy(authenticationService.getCurrentUserName());//scheduler.setUpdatedBy(userService.getCurrentPersonName());
        try {
            schedulerRepository.save(scheduler);
            return true;
        } catch (Exception e) {
            throw new InvalidResourceDetailsException("Scheduler","An error occurred while updating the user with new personaList:"+e);
        }
    }
    /**
     * Updates the schedule with the specified ID by modifying both its start and end times and associated user list.
     * @param id              The ID of the schedule to be updated.
     * @param newPersonIdList The list of IDs of users to be associated with the schedule.
     * @param newStartTime    The new start time for the schedule.
     * @param newEndTime      The new end time for the schedule.
     * @param updatedBy       The user updating the schedule.
     * @return True if the update is successful, false otherwise.
     * @throws InvalidResourceDetailsException If an error occurs during the update.
     */
    public Boolean updateScheduleWithNewTimeAndPersons(Long id,List<Long> newPersonIdList,String newStartTime,String newEndTime,String updatedBy) {
        Boolean scheduleWithPersons;
        Boolean scheduleWithTime;
        try {
            scheduleWithPersons = updateScheduleWithPerson(id, newPersonIdList);
            scheduleWithTime = updateScheduleWithTime(id, newStartTime, newEndTime);
        } catch (Exception e) {
            throw new InvalidResourceDetailsException("Scheduler","An error occurred while updating user with time and personList"+e);
        }
        return scheduleWithPersons && scheduleWithTime;
    }
}