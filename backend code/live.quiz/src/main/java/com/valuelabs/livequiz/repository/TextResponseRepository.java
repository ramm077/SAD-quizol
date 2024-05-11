package com.valuelabs.livequiz.repository;

import com.valuelabs.livequiz.model.entity.TextResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * The TextResponseRepository interface provides database access operations for TextResponse entities.
 */
@Repository
public interface TextResponseRepository extends JpaRepository<TextResponse,Long> {
    /**
     * Finds a text response by its textResponseId and inActive status.
     * @param textResponseId The ID of the text response.
     * @param inActive       The inActive status of the text response.
     * @return Optional containing the found text response, or empty if not found.
     */
    Optional<TextResponse> findByTextResponseIdAndInActive(Long textResponseId, Boolean inActive);
}
