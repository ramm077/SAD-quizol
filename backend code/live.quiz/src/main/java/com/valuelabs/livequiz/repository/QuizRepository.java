package com.valuelabs.livequiz.repository;

import com.valuelabs.livequiz.model.entity.Quiz;
import com.valuelabs.livequiz.model.enums.QuizType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * The QuizRepository interface provides database access operations for Quiz entities.
 */
@Repository
public interface QuizRepository extends JpaRepository<Quiz,Long> {
    /**
     * Finds a quiz by its quizId and inActive status.
     * @param quizId   The ID of the quiz.
     * @param inActive The inActive status of the quiz.
     * @return Optional containing the found quiz, or empty if not found.
     */
    Optional<Quiz> findByQuizIdAndInActive(Long quizId, Boolean inActive);
    /**
     * Retrieves all quizzes with the specified inActive status.
     * @param inActive The inActive status of the quizzes to retrieve.
     * @return List of quizzes matching the inActive status.
     */
    List<Quiz> findAllByInActive(Boolean inActive);
    /**
     * Finds a quiz by its quizName and inActive status.
     * @param quizName The name of the quiz.
     * @param inActive The inActive status of the quiz.
     * @return The found quiz, or null if not found.
     */
    Quiz findByQuizNameAndInActive(String quizName, boolean inActive);
    /**
     * Retrieves all quizzes with the specified quizType and inActive status.
     * @param quizType The type of quizzes to retrieve.
     * @param inActive The inActive status of the quizzes to retrieve.
     * @return List of quizzes matching the specified quizType and inActive status.
     */
    List<Quiz> findAllByQuizTypeAndInActive(QuizType quizType, Boolean inActive);
}
