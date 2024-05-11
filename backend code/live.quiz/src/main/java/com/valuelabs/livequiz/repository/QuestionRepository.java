package com.valuelabs.livequiz.repository;

import com.valuelabs.livequiz.model.entity.Question;
import com.valuelabs.livequiz.model.enums.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * The QuestionRepository interface provides database access operations for Question entities.
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question,Long> {
    /**
     * Retrieves all questions with the specified inActive status.
     * @param inActive The inActive status of the questions to retrieve.
     * @return List of questions matching the inActive status.
     */
    List<Question> findAllByInActive(Boolean inActive);
    /**
     * Finds a question by its questionId and inActive status.
     * @param questionId The ID of the question.
     * @param inActive   The inActive status of the question.
     * @return Optional containing the found question, or empty if not found.
     */
    Optional<Question> findByQuestionIdAndInActive(Long questionId, Boolean inActive);
    /**
     * Retrieves all questions with the specified questionType and inActive status.
     * @param questionType The type of questions to retrieve.
     * @param inActive     The inActive status of the questions to retrieve.
     * @return List of questions matching the specified questionType and inActive status.
     */
    List<Question> findAllByQuestionTypeAndInActive(QuestionType questionType, Boolean inActive);
}
