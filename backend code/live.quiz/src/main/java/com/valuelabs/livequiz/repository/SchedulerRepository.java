package com.valuelabs.livequiz.repository;

import com.valuelabs.livequiz.model.entity.Quiz;
import com.valuelabs.livequiz.model.entity.Scheduler;
import com.valuelabs.livequiz.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * The SchedulerRepository interface provides database access operations for Scheduler entities.
 */
@Repository
public interface SchedulerRepository extends JpaRepository<Scheduler,Long> {
    /**
     * Retrieves all schedulers with the specified inActive status.
     * @param inActive The inActive status of the schedulers to retrieve.
     * @return List of schedulers matching the specified inActive status.
     */
    List<Scheduler> findAllByInActive(boolean inActive);
    /**
     * Retrieves all schedulers associated with a quiz and with the specified inActive status.
     * @param quiz     The quiz associated with the schedulers.
     * @param inActive The inActive status of the schedulers to retrieve.
     * @return List of schedulers matching the specified quiz and inActive status.
     */
    List<Scheduler> findAllByQuizAndInActive(Quiz quiz, boolean inActive);
    /**
     * Finds a scheduler by its schedulerId and inActive status.
     * @param schedulerId The ID of the scheduler.
     * @param inActive    The inActive status of the scheduler.
     * @return Optional containing the found scheduler, or empty if not found.
     */

    Optional<Scheduler> findBySchedulerIdAndInActive(Long schedulerId, boolean inActive);
    /**
     * Retrieves all schedulers associated with a quiz and containing a specified user.
     * @param quiz The quiz associated with the schedulers.
     * @param user The user to check for in the scheduler's userList.
     * @return List of schedulers associated with the quiz and containing the specified user.
     */
    List<Scheduler> findByQuizAndUserListContaining(Quiz quiz, User user);


}
