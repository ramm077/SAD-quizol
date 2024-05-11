package com.valuelabs.livequiz.service.option;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.dto.response.DisplayOptionDTO;
import com.valuelabs.livequiz.model.entity.Option;
import com.valuelabs.livequiz.repository.OptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.valuelabs.livequiz.exception.ExceptionUtility.throwInvalidResourceDetailsException;
import static com.valuelabs.livequiz.exception.ExceptionUtility.throwResourceNotFoundException;

/**
 * The DisplayOptionService class handles the retrieval and response of options.
 */
@Service
@Slf4j
public class DisplayOptionService {
    private final OptionRepository optionRepository;
    /**
     * Constructor to initialize DisplayOptionService with OptionRepository.
     * @param optionRepository The repository for Option entities.
     */
    @Lazy
    @Autowired
    public DisplayOptionService(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }
    /**
     * Retrieves an option by its ID.
     * @param optionId  The ID of the option to retrieve.
     * @param inActive  Specifies whether to fetch active or inactive options.
     * @return The Option object fetched by ID.
     * @throws ResourceNotFoundException If no matching option is found.
     * @throws InvalidResourceDetailsException If the provided optionId is null.
     */
    public Option getOptionById(Long optionId, Boolean inActive){
        log.info("Inside DisplayOptionService, getOptionById method!");
        if(optionId != null){
            log.debug("Fetching the Option with optionId: "+ optionId);
            return optionRepository.findByOptionIdAndInActive(optionId,inActive).orElseThrow(() -> {
                     throwResourceNotFoundException("Option", "No "+(inActive ? "inactive" : "active") + " option found with optionId " + optionId);
                return null; });
        } else {
            log.info("Option Id not provided, throwing InvalidResourceDetailsException! ");
            throwInvalidResourceDetailsException("Option", "Option Id must be provided!");
        }
        return null;
    }
    /**
     * Displays details of a specific option.
     * @param optionId  The ID of the option to response.
     * @param inActive  Specifies whether to fetch active or inactive options.
     * @return A DisplayOptionDTO object containing option details.
     * @throws InvalidResourceDetailsException If the provided optionId is null.
     */
    public DisplayOptionDTO displayOption(Long optionId,Boolean inActive){
        log.info("Inside DisplayOptionService, getOptionById method!");
        log.debug("Fetching the Option with optionId: "+ optionId);
        Option option = getOptionById(optionId,inActive);
        log.debug("Option "+ option.getOptionText()+" this successfully fetched!, displaying option");
        return new DisplayOptionDTO(optionId, option.getOptionText(), option.getIsTrue());
    }
    /**
     * Displays a list of options in a question.
     * @param optionList    The list of Option objects to response.
     * @param inActive      Specifies whether to fetch active or inactive options.
     * @return A set of DisplayOptionDTO objects containing option details.
     * @throws InvalidResourceDetailsException If the provided optionList is null.
     */
    public List<DisplayOptionDTO> displayOptionsInQuestion(List<Option> optionList, Boolean inActive){
        log.info("Inside DisplayOptionService, displayOptionsInQuestion method!");
        if(optionList != null) {
            log.debug("Trying to fetch the options through the given optionList!");
            return optionList.stream().map(option -> displayOption(option.getOptionId(), inActive))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
