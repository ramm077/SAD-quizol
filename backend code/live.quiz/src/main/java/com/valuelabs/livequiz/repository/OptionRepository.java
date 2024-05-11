package com.valuelabs.livequiz.repository;

import com.valuelabs.livequiz.model.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * The OptionRepository interface provides database access operations for Option entities.
 */
@Repository
public interface OptionRepository extends JpaRepository<Option,Long> {
    /**
     * Finds an Option by its optionId and inActive status.
     * @param optionId The ID of the Option.
     * @param inActive The inActive status of the Option.
     * @return Optional containing the found Option, or empty if not found.
     */
    Optional<Option> findByOptionIdAndInActive(Long optionId, Boolean inActive);
}
