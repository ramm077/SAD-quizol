package com.valuelabs.livequiz.service.scheduler;
import com.valuelabs.livequiz.model.dto.request.ScheduleRequestDTO;
import com.valuelabs.livequiz.model.dto.response.DisplayScheduleDTO;
import com.valuelabs.livequiz.model.dto.response.QuizScheduleAdminDTO;
import com.valuelabs.livequiz.model.dto.response.QuizScheduleDTO;
import com.valuelabs.livequiz.model.entity.Quiz;
import com.valuelabs.livequiz.model.entity.Scheduler;
import com.valuelabs.livequiz.model.entity.User;
import com.valuelabs.livequiz.repository.SchedulerRepository;
import com.valuelabs.livequiz.service.quiz.DisplayQuizService;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import com.valuelabs.livequiz.service.user.DisplayUserService;
import com.valuelabs.livequiz.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.valuelabs.livequiz.exception.ExceptionUtility.*;
import static com.valuelabs.livequiz.utils.InputValidator.validateDTO;

/**
 * This class is used to handle the create operations of scheduler entity, also to fetch the scheduler details also displaying them.
 */
@Lazy
@Service
@Slf4j
public class CreateSchedulerService {
    private final ScheduleReminderService scheduleReminderService;
    private final SchedulerRepository schedulerRepository;
    private final DisplayQuizService displayQuizService;
    private final DisplayUserService displayUserService;
    private final AuthenticationService authenticationService;
    @Lazy
    @Autowired
    public CreateSchedulerService(ScheduleReminderService scheduleReminderService, SchedulerRepository schedulerRepository, DisplayQuizService displayQuizService, DisplayUserService displayUserService, AuthenticationService authenticationService) {
        this.scheduleReminderService = scheduleReminderService;
        this.schedulerRepository = schedulerRepository;
        this.displayQuizService = displayQuizService;
        this.displayUserService = displayUserService;
        this.authenticationService = authenticationService;
    }
    /**
     * Endpoint for creating a schedule with associated persons based on the provided details.
     * @param scheduleRequestDTO The Data Transfer Object (DTO) containing schedule creation details.
     * @return Scheduler entity representing the created schedule.
     * @throws InvalidResourceDetailsException if an exception occurs during the schedule creation process.
     */
    public Scheduler createScheduleWithUsers(ScheduleRequestDTO scheduleRequestDTO){
        log.info("Creating a complete schedule along with sending notification to users");
        validateDTO(scheduleRequestDTO);
        log.debug("The create request DTO is validated successfully");
        List<User> userList = new ArrayList<>();
        Timestamp startTimeT=Timestamp.valueOf(scheduleRequestDTO.startTime());
        Timestamp endTimeT=Timestamp.valueOf(scheduleRequestDTO.endTime());
        Quiz quiz = displayQuizService.getQuizById(scheduleRequestDTO.quizId(),false);
        log.debug("Fetched the quiz by quiz id successfully:"+scheduleRequestDTO.quizId());
        Scheduler scheduler = new Scheduler(startTimeT,endTimeT,quiz);
        scheduler.setCreatedBy(authenticationService.getCurrentUserName());//scheduler.setCreatedBy(userService.getCurrentPersonName());
        for(Long userId:scheduleRequestDTO.personIdList()){
            User user = displayUserService.getUserByIdAndInActive(userId,false);
            log.debug("Fetched the user with user id successfully:"+userId);
            List<Scheduler> schedulerList = schedulerRepository.findByQuizAndUserListContaining(quiz,user);
            if(schedulerList != null && !schedulerList.isEmpty()){
                Timestamp previousEndTime = Timestamp.valueOf(schedulerList.get(schedulerList.size()-1).getEndTime().toLocalDateTime());
                if(previousEndTime.getTime() < startTimeT.getTime()){
                    userList.add(user);
                } else throwResourceExistsException("Scheduler","This quiz "+quiz.getQuizName()+" is already been scheduled for the user "+user.getFirstName());

            }else userList.add(user);
        }
        scheduler.setUserList(userList);
        try {
            log.info("Creating a complete schedule along with sending notification to users");
            scheduler = schedulerRepository.save(scheduler);
            log.debug("Schedule is saved successfully:");
            scheduleReminderService.scheduleSendCreateScheduler(scheduler);
            scheduleReminderService.scheduleReminders(scheduler);
            return scheduler;
        }catch (Exception e){
            log.error("Schedule creating caused an error:"+e);
            throwInvalidResourceDetailsException("Scheduler","Invalid schedule Cannot be saved:"+e);
            return null;
        }
    }
    /**
     * Retrieve all active schedules associated with a specific person based on the provided personId.
     * @param userId The unique identifier of the person for whom schedules are to be retrieved.
     * @return List of Scheduler entities representing active schedules associated with the specified person.
     * @throws ResourceNotFoundException if an error occurs during the retrieval process.
     */
    public List<QuizScheduleDTO> getAllActiveSchedulesByPerson(Long userId) {
        log.info("Fetching all Active schedules for a user:"+userId);
        try {
            User user = displayUserService.getUserByIdAndInActive(userId,false);
            log.debug("Fetched the user details by user id successfully:"+userId);
            List<Scheduler> allSchedulerList= schedulerRepository.findAllByInActive(false);
            log.debug("Fetched all the Active schedules successfully:"+userId);
            List<Scheduler> schedulerListForAPerson = new ArrayList<>();
            for(Scheduler scheduler: allSchedulerList){
                if(scheduler.getUserList().contains(user)){
                    schedulerListForAPerson.add(scheduler);
                }
            }
            log.debug("Fetched all schedules for the particular user");
            return schedulerListForAPerson.stream().map(scheduler -> new QuizScheduleDTO(scheduler.getSchedulerId(),String.valueOf(scheduler.getStartTime()),String.valueOf(scheduler.getEndTime()),scheduler.getQuiz().getQuizId(),scheduler.getQuiz().getQuizName(),scheduler.getQuiz().getQuizType().toString(),scheduler.getQuiz().getQuestionList().size())).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Fetching all Schedules by userId caused an error:"+e);
            throwResourceNotFoundException("Scheduler","An error occurred while finding list of active schedules for a user: "+e);
            return null;
        }
    }
    /**
     * Retrieves all active schedules associated with a specific quiz based on the provided quizId.
     * @param quizId The unique identifier of the quiz for which schedules are to be retrieved.
     * @return List of QuizScheduleDTO representing active schedules associated with the specified quiz.
     * @throws ResourceNotFoundException if an error occurs during the retrieval process.
     */
    public List<QuizScheduleDTO> getAllActiveSchedulesByQuiz(Long quizId) {
        log.info("Fetching all Active schedules for a quiz:"+quizId);
        try {
            Quiz quiz =displayQuizService.getQuizById(quizId,false);
            log.debug("Fetched the quiz details by quiz id successfully:"+quizId);
            List<Scheduler> schedulerListForAQuiz= schedulerRepository.findAllByQuizAndInActive(quiz,false);
            log.debug("Fetched all schedules for a quiz successfully");
            return schedulerListForAQuiz.stream().map(scheduler -> new QuizScheduleDTO(scheduler.getSchedulerId(),String.valueOf(scheduler.getStartTime()),String.valueOf(scheduler.getEndTime()),scheduler.getQuiz().getQuizId(),scheduler.getQuiz().getQuizName(),scheduler.getQuiz().getQuizType().toString(),scheduler.getQuiz().getQuestionList().size())).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Fetching all Schedules by quizId caused an error:"+e);
            throwResourceNotFoundException("Scheduler","An error occurred while finding list of active schedules for a quiz: "+e);
            return null;
        }
    }
    /**
     * Retrieves all active schedules in the system.
     * @return List of QuizScheduleDTO representing all active schedules.
     * @throws ResourceNotFoundException if an error occurs during the retrieval process.
     */
    public List<QuizScheduleAdminDTO> getAllActiveSchedules(){
        log.info("Fetching all Active schedules ");
        try {
            List<Scheduler> schedulerList = schedulerRepository.findAllByInActive(false);
            log.debug("All active schedules fetched successfully");
            List<QuizScheduleAdminDTO> quizScheduleDTOList = getQuizScheduleAdminDTOS(schedulerList);
            log.debug("DTO created successfully");
            return quizScheduleDTOList;
        } catch (Exception e) {
            log.error("Fetching all active schedules cause exception");
            throwResourceNotFoundException("Scheduler","An error occurred while finding list of active schedules: "+e);
            return null;
        }
    }

    /**
     * This method is used to display all the schedules for the admin dashboard.
     * @param schedulerList is given inorder to convert them to display DTO's.
     * @return List of QuizScheduleAdminDTO for the admin dashboard.
     */
    private static List<QuizScheduleAdminDTO> getQuizScheduleAdminDTOS(List<Scheduler> schedulerList) {
        List<QuizScheduleAdminDTO> quizScheduleDTOList = new ArrayList<>();
        for(Scheduler scheduler: schedulerList) {
            QuizScheduleAdminDTO quizScheduleDTO = new QuizScheduleAdminDTO(scheduler.getSchedulerId(),String.valueOf(scheduler.getStartTime()),String.valueOf(scheduler.getEndTime()),scheduler.getQuiz().getQuizId(),scheduler.getQuiz().getQuizName(),scheduler.getQuiz().getQuizType().toString(),scheduler.getQuiz().getQuestionList().size(),scheduler.getUserList().size());
            quizScheduleDTOList.add(quizScheduleDTO);
        }
        return quizScheduleDTOList;
    }
    /**
     * Retrieves details of active Users associated with a specific scheduler based on the provided scheduleId.
     * @param schedulerId The unique identifier of the schedule for which details are to be retrieved.
     * @return List of DisplayScheduleDTO representing details of active Person for the specified scheduler.
     * @throws ResourceNotFoundException if the specified schedule is not found.
     */
    public List<DisplayScheduleDTO> getAllUsersInfoBySchedule(Long schedulerId) {
        log.info("Fetch all the user data for a scheduler with schedulerId:"+schedulerId);
        Scheduler scheduler = schedulerRepository.findBySchedulerIdAndInActive(schedulerId,false)
                .orElseThrow(() -> {throwResourceNotFoundException("Scheduler","Schedule not found with id:"+schedulerId); return null;});
        List<DisplayScheduleDTO> displayScheduleDTOList = new ArrayList<>();
        for(User user : scheduler.getUserList()){
            displayScheduleDTOList.add(new DisplayScheduleDTO(user.getUserId(), user.getFirstName(), user.getLastName(), user.getEmailId(), user.getRole()));
        }
        log.debug("Fetch all the user data for a scheduler with schedulerId:"+schedulerId);
        return displayScheduleDTOList;
    }
    /**
     * This method is used to get scheduler by its Id.
     * @param schedulerId is used to fetch the active scheduler.
     * @return scheduler if it is not null.
     */
    public Scheduler getScheduleById(Long schedulerId){
        if(schedulerId != null){
            return schedulerRepository.findBySchedulerIdAndInActive(schedulerId,false).orElseThrow(() -> {throwResourceNotFoundException("Scheduler","Schedule not found with id:"+schedulerId); return null;});
        }
        return null;
    }
}
