package com.valuelabs.livequiz.controller;

import com.valuelabs.livequiz.model.dto.request.DeleteScheduleRequestDTO;
import com.valuelabs.livequiz.model.dto.request.ScheduleRequestDTO;
import com.valuelabs.livequiz.model.dto.request.UpdateScheduleRequestDTO;
import com.valuelabs.livequiz.model.dto.response.DisplayScheduleDTO;
import com.valuelabs.livequiz.model.dto.response.QuizScheduleAdminDTO;
import com.valuelabs.livequiz.model.dto.response.QuizScheduleDTO;
import com.valuelabs.livequiz.model.entity.Scheduler;
import com.valuelabs.livequiz.service.scheduler.CreateSchedulerService;
import com.valuelabs.livequiz.service.scheduler.UpdateSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for handling scheduler-related operations.
 */
@RestController
@RequestMapping("/api/schedule")
@CrossOrigin
@Slf4j
public class SchedulerController {
    private final CreateSchedulerService createSchedulerService;
    private final UpdateSchedulerService updateSchedulerService;
    @Autowired
    public SchedulerController(CreateSchedulerService createSchedulerService, UpdateSchedulerService updateSchedulerService) {
        this.createSchedulerService = createSchedulerService;
        this.updateSchedulerService = updateSchedulerService;
    }

    /**
     * Endpoint for creating a schedule with the provided details.
     * @param scheduleRequestDTO The Data Transfer Object (DTO) containing schedule creation details.
     * @return ResponseEntity with the created Scheduler entity if the schedule is successfully created,
     *         or HTTP 400 Bad Request status with an error message if creation fails.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createSchedule(@RequestBody ScheduleRequestDTO scheduleRequestDTO){
        log.info("Creating Schedule for a quiz");
        log.debug("Validating the create schedule request DTO:"+scheduleRequestDTO);
        Scheduler scheduler = createSchedulerService.createScheduleWithUsers(scheduleRequestDTO);
        if(scheduler!=null){
            log.debug("Schedule created with scheduler Id:"+scheduler.getSchedulerId());
            return ResponseEntity.ok(scheduler);
        }
        log.error("Schedule could not be created.");
        return ResponseEntity.badRequest().body("Schedule can't be created");
    }
    /**
     * Endpoint for retrieving schedule details based on the provided scheduleId.
     * @param schedulerId The unique identifier of the schedule to be retrieved.
     * @return ResponseEntity containing a list of SchedulePersonResponseDTOs with person details associated with the schedule.
     *         Returns HTTP 200 OK status if retrieval is successful, otherwise returns HTTP 400 Bad Request status with an error message.
     */
    @GetMapping("/allUsers")
    public ResponseEntity<?> getScheduleById(@RequestParam Long schedulerId){
        log.info("Fetching a Schedule by it's id:"+schedulerId);
        try {
            log.debug("Trying to fetch the scheduler data from schedule Id");
            List<DisplayScheduleDTO> displayScheduleDTOList = createSchedulerService.getAllUsersInfoBySchedule(schedulerId);
            log.debug("Fetched the scheduler data successfully");
            return ResponseEntity.ok(displayScheduleDTOList);
        } catch (Exception e) {
            log.error("Couldn't fetch the scheduler data due to error:"+e);
            return ResponseEntity.badRequest().body("Getting the schedule by id caused exception:"+e);
        }
    }

    /**
     * Endpoint for retrieving all active schedules associated with a specific person based on the provided personId.
     * @param userId The unique identifier of the person for whom schedules are to be retrieved.
     * @return ResponseEntity containing a list of ScheduleResponseDTOs with schedule details related to the specified person.
     *         Returns HTTP 200 OK status if retrieval is successful, otherwise returns HTTP 400 Bad Request status with an error message.
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllSchedulesByPerson(@RequestParam Long userId){
        log.info("Fetching all Schedules for a user with it's id: "+userId);
        try {
            log.debug("Trying to fetch all scheduler data list for a user with user Id");
            List<QuizScheduleDTO> quizScheduleDTOList = createSchedulerService.getAllActiveSchedulesByPerson(userId);
            log.debug("Fetched the scheduler data successfully");
            return ResponseEntity.ok(quizScheduleDTOList);
        }catch (Exception e){
            log.error("Couldn't fetch the schedules data for a quiz due to error:"+e);
            return ResponseEntity.badRequest().body("Getting the schedules for a particular person caused exception:"+e);
        }
    }
    /**
     * Endpoint for retrieving details of all active schedules.
     * @return ResponseEntity containing a list of ScheduleResponseDTOs with details such as schedulerId, startTime, endTime,
     *         quizId, quizName, and quizType for each schedule.
     *         Returns HTTP 200 OK status if retrieval is successful, otherwise returns HTTP 400 Bad Request status with an error message.
     */
    @GetMapping("/allSchedules")
    public ResponseEntity<?> getAllSchedules(){
        log.info("Fetching all active Schedules ");
        try {
            log.debug("Trying to fetch all active scheduler data ");
            List<QuizScheduleAdminDTO> quizScheduleDTOList = createSchedulerService.getAllActiveSchedules();
            log.debug("Fetched the scheduler data successfully");
            return ResponseEntity.ok(quizScheduleDTOList);
        } catch (Exception e) {
            log.error("Couldn't fetch all the schedules data due to error:"+e);
            return ResponseEntity.badRequest().body("Getting the schedules caused exception:"+e);
        }
    }
    /**
     * Endpoint for updating a schedule with a reason based on the provided scheduleId.
     * @param schedulerId The unique identifier of the schedule to be updated.
     * @param updateScheduleRequestDTO The Data Transfer Object (DTO) containing the updated information, including a reason.
     * @return ResponseEntity with an appropriate message indicating the result of the update operation.
     *         Returns HTTP 200 OK status if update is successful, HTTP 404 Not Found status if the schedule is not found,
     *         or HTTP 400 Bad Request status with an error message if an exception occurs during the update process.
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateScheduleWithReason(@RequestParam Long schedulerId, @RequestBody UpdateScheduleRequestDTO updateScheduleRequestDTO){
        log.info("Updating a Schedule by it's id:"+schedulerId);
        try {
            log.debug("Trying to update active scheduler data by it's id ");
            Boolean isUpdated = updateSchedulerService.updateScheduleWithReason(schedulerId, updateScheduleRequestDTO);
            if(isUpdated){
                log.debug("updated the scheduler data successfully");
                return ResponseEntity.ok("Schedule updated with the reason");
            } else if (!isUpdated) {
                log.debug("Schedule can't be Updated since already a user has responded");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Schedule can't be updated since user has already responded");

            }
            log.debug("Schedule not found with the given id:"+schedulerId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schedule is not found");
        } catch (Exception e) {
            log.error("Couldn't update the schedule data by it's id due to error:"+e);
            return ResponseEntity.badRequest().body("Schedule couldn't be updated due to an error:"+e);
        }
    }
    /**
     * Endpoint for deleting a schedule with a reason based on the provided scheduleId.
     * @param schedulerId The unique identifier of the schedule to be deleted.
     * @param deleteScheduleRequestDTO The Data Transfer Object (DTO) containing the reason for deleting the schedule.
     * @return ResponseEntity with an appropriate message indicating the result of the delete operation.
     *         Returns HTTP 200 OK status if deletion is successful, or HTTP 400 Bad Request status with an error message if an exception occurs during the delete process.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteScheduleWithReason(@RequestParam Long schedulerId, @RequestBody DeleteScheduleRequestDTO deleteScheduleRequestDTO){
        log.info("deleting a Schedule by it's id:"+schedulerId);
        try {
            log.debug("Trying to delete active scheduler data by it's id ");
            Boolean deleted = updateSchedulerService.deleteScheduleById(schedulerId, deleteScheduleRequestDTO.reason());
            if(!deleted){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Schedule can't be Updated since already a user has responded");
            }
            log.debug("deleted the scheduler data successfully");
            return ResponseEntity.ok("Schedule has been deleted");
        } catch (Exception e) {
            log.error("Couldn't update the schedule data by it's id due to error:"+e);
            return ResponseEntity.badRequest().body("Schedule couldn't be deleted due to an error:"+e);
        }
    }
}