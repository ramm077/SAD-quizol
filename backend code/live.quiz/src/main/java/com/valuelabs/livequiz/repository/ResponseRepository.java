package com.valuelabs.livequiz.repository;

import com.valuelabs.livequiz.model.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * The ResponseRepository interface provides database access operations for Response entities.
 */
@Repository
public interface ResponseRepository extends JpaRepository<Response,Long> {
    /**
     * Finds a response by user, scheduler, and question.
     * @param user      The user associated with the response.
     * @param scheduler The scheduler associated with the response.
     * @param question  The question associated with the response.
     * @return The found response, or null if not found.
     */
    Response findByUserAndSchedulerAndQuestionAndInActive(User user, Scheduler scheduler, Question question, Boolean inActive);
    /**
     * Finds a response by its responseId and inActive status.
     * @param responseId The ID of the response.
     * @param inActive   The inActive status of the response.
     * @return Optional containing the found response, or empty if not found.
     */
    Optional<Response> findByResponseIdAndInActive(Long responseId, Boolean inActive);
    /**
     * Retrieves all responses for a scheduler with the specified inActive status.
     * @param scheduler The scheduler for which responses are retrieved.
     * @param inActive  The inActive status of the responses to retrieve.
     * @return List of responses matching the specified scheduler and inActive status.
     */
    List<Response> findAllBySchedulerAndInActive(Scheduler scheduler, Boolean inActive);


}
