package com.valuelabs.livequiz.repository;

import com.valuelabs.livequiz.model.entity.OptionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * The OptionResponseRepository interface provides database access operations for OptionResponse entities.
 */
@Repository
public interface OptionResponseRepository extends JpaRepository<OptionResponse,Long> {
    /**
     * Finds an OptionResponse by its optionResponseId and inActive status.
     * @param optionResponseId The ID of the OptionResponse.
     * @param inActive         The inActive status of the OptionResponse.
     * @return Optional containing the found OptionResponse, or empty if not found.
     */
    Optional<OptionResponse> findByOptionResponseIdAndInActive(Long optionResponseId, Boolean inActive);
}
