package com.valuelabs.livequiz.service.scheduler;

import com.valuelabs.livequiz.model.entity.Scheduler;
import com.valuelabs.livequiz.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import static com.valuelabs.livequiz.model.constants.AppConstants.REMINDER_TIME;

/**
 * This class is used to send the remainder mails based on the requirement, like creation, remainder, updation, deletion
 */
@Lazy
@Service
@Slf4j
public class ScheduleReminderService {
    private final TaskScheduler taskScheduler;
    private final SchedulerEmailService schedulerEmailService;
    @Lazy
    @Autowired
    public ScheduleReminderService(TaskScheduler taskScheduler, SchedulerEmailService schedulerEmailService) {
        this.taskScheduler = taskScheduler;
        this.schedulerEmailService = schedulerEmailService;
    }
    private final Map<Long, ScheduledFuture<?>> scheduledReminders = new HashMap<>();

    /**
     * Scheduling task scheduler to send quiz schedule notification to the responders
     * @param scheduler
     */
    public void scheduleSendCreateScheduler(Scheduler scheduler){
        Instant currentTime = Instant.now();
        taskScheduler.schedule(()-> sendQuizScheduledEmailToResponders(scheduler),currentTime);
    }
    /**
     * Scheduling task scheduler to send quiz schedule cancellation notification to the responders
     * @param scheduler
     */
    public void scheduleSendCancelScheduler(Scheduler scheduler,String reason){
        Instant currentTime = Instant.now();
        taskScheduler.schedule(()-> sendQuizCancellationEmailToResponders(scheduler,reason),currentTime);
    }
    /**
     * @param existingUserList existing userList
     * @param addUserList newly added userList
     * @param deleteUserList deleted userList
     * @param scheduler The scheduler for which the update is done
     */
    public void scheduleSendUpdateScheduler(List<User> existingUserList, List<User> addUserList, List<User> deleteUserList, Scheduler scheduler){
        Instant currentTime = Instant.now();
        taskScheduler.schedule(()-> sendUpdatedQuizScheduledEmailToResponders(existingUserList,addUserList,deleteUserList,scheduler),currentTime);
    }
    /**
     *  Schedules reminders for a quiz based on its start time.
     * @param scheduler The scheduler containing details about the quiz and its start time.
     * @constant REMINDER_TIME
     * @implNote Calculates the reminder time 45 minutes before the quiz's start time.
     * Uses TaskScheduler to schedule the reminder task and stores it in a map.
     **/
    public void scheduleReminders(Scheduler scheduler) {
        log.info("Scheduling reminder to be executed 45 minutes before start time");
        // Schedule reminders based on startTime - REMINDER_TIME minutes
        //Timestamp reminderTime = new Timestamp(scheduler.getStartTime().getTime() - REMINDER_TIME);
        Instant reminderTime = scheduler.getStartTime().toInstant().minus(Duration.ofMinutes(REMINDER_TIME));
        log.info("Reminder time:"+REMINDER_TIME);

        // Use TaskScheduler to schedule the reminder
        ScheduledFuture<?> reminderTask = taskScheduler.schedule(() -> sendRemindersEmailToResponders(scheduler,REMINDER_TIME), reminderTime);
        // Store the reminder task in a map
        scheduledReminders.put(scheduler.getSchedulerId(), reminderTask);
    }
    /**
     * Cancels a scheduled reminder task based on its identifier.
     * @param id The identifier of the scheduled reminder task.
     * @implNote Cancels the existing task if it's not already cancelled and removes it from the map.
     */
    public void cancelReminderTask(Long id) {
        ScheduledFuture<?> existingTask = scheduledReminders.get(id);
        if (existingTask != null && !existingTask.isCancelled()) {
            existingTask.cancel(true);
            scheduledReminders.remove(id);
        }
    }
    /**
     * Sends reminder emails to all responders scheduled for a quiz.
     * @param scheduler    The scheduler containing details about the quiz and its responders.
     * @param reminderTime
     * @implNote Iterates through the list of users scheduled for the quiz and sends reminder emails.
     */
    public void sendRemindersEmailToResponders(Scheduler scheduler, Integer reminderTime) {
        log.info("Scheduling reminder mail");
        for(User user : scheduler.getUserList()){
            schedulerEmailService.sendQuizReminderEmail(user,scheduler.getQuiz().getQuizName(),scheduler.getQuiz().getQuizType().toString(),scheduler.getStartTime(),reminderTime);
        }
    }
    /**
     * Sends quiz scheduled notification emails to all responders scheduled for a quiz.
     * @param scheduler The scheduler containing details about the quiz and its responders.
     * @implNote Iterates through the list of users scheduled for the quiz and sends scheduled notification emails.
     */
    public void sendQuizScheduledEmailToResponders(Scheduler scheduler) {
        log.info("sending quiz scheduled mail to responders");
        for(User user : scheduler.getUserList()){
            schedulerEmailService.sendQuizScheduleEmailToResponder(user,scheduler.getQuiz().getQuizName(),scheduler.getQuiz().getQuizType().toString(),scheduler.getStartTime(),scheduler.getEndTime());
        }
    }
    /**
     * Sends updated quiz scheduled notification emails to all responders scheduled for a quiz.
     * @param existinguserList The existing userList
     * @param addUserList The newly added userList
     * @param deleteUserList The deleted userList
     * @param scheduler  The scheduler containing details about the quiz and its responders.
     * @implNote Iterates through the list of users scheduled for the quiz and sends updated scheduled notification emails.
     */
    public void sendUpdatedQuizScheduledEmailToResponders(List<User> existinguserList, List<User> addUserList, List<User> deleteUserList, Scheduler scheduler){
        log.info("Sending updated mail to responders along with scheduled mail for new users and cancellation mail for deleted users");
        existinguserList.removeAll(deleteUserList);
        existinguserList.removeAll(addUserList);
        addUserList.removeAll(existinguserList);
        addUserList.removeAll(deleteUserList);
        deleteUserList.removeAll(existinguserList);
        deleteUserList.removeAll(addUserList);
        for(User user:existinguserList){
            log.debug("The User with id"+user.getUserId()+"will be sent Updated Quiz scheduled notification mail");
            schedulerEmailService.sendUpdatedScheduleEmail(user, scheduler.getQuiz().getQuizName(), scheduler.getQuiz().getQuizType().toString(), scheduler.getStartTime(), scheduler.getEndTime());
        }
        for(User user: addUserList){
            log.debug("The User with id"+user.getUserId()+"will be sent Quiz scheduled notification mail");
            schedulerEmailService.sendQuizScheduleEmailToResponder(user, scheduler.getQuiz().getQuizName(), scheduler.getQuiz().getQuizType().toString(), scheduler.getStartTime(), scheduler.getEndTime());
        }
        for (User user: deleteUserList){
            log.debug("The User with id"+user.getUserId()+"will be sent Quiz scheduled cancellation notification mail");
            schedulerEmailService.sendQuizCancellationEmail(user, scheduler.getQuiz().getQuizName(), scheduler.getQuiz().getQuizType().toString(), scheduler.getReason());

        }
    }
    /**
     * Sends quiz cancellation notification emails to all responders scheduled for a quiz.
     * @param scheduler The scheduler containing details about the quiz and its responders.
     * @param reason The reason for quiz cancellation.
     * @implNote Iterates through the list of users scheduled for the quiz and sends cancellation notification emails.
     */
    public void sendQuizCancellationEmailToResponders(Scheduler scheduler, String reason) {
        for(User user :scheduler.getUserList()){
            schedulerEmailService.sendQuizCancellationEmail(user,scheduler.getQuiz().getQuizName(),scheduler.getQuiz().getQuizType().toString(), scheduler.getReason());
        }
    }
}
