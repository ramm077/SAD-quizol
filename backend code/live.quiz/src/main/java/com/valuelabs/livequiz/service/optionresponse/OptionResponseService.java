package com.valuelabs.livequiz.service.optionresponse;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.entity.Option;
import com.valuelabs.livequiz.model.entity.OptionResponse;
import com.valuelabs.livequiz.repository.OptionResponseRepository;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.valuelabs.livequiz.exception.ExceptionUtility.throwInvalidResourceDetailsException;
import static com.valuelabs.livequiz.exception.ExceptionUtility.throwResourceNotFoundException;
/**
 * The OptionResponseService class provides methods for managing option responses.
 * It interacts with the option response repository and authentication service.
 */
@Service
@Slf4j
public class OptionResponseService {
    private final OptionResponseRepository optionResponseRepository;
    private final AuthenticationService authenticationService;
    @Autowired
    public OptionResponseService(OptionResponseRepository optionResponseRepository, AuthenticationService authenticationService) {
        this.optionResponseRepository = optionResponseRepository;
        this.authenticationService = authenticationService;
    }
    /**
     * Creates an option response based on the provided list of chosen options.
     * @param chosenOptions The list of options chosen by the user.
     * @return The created option response.
     * @throws InvalidResourceDetailsException If no chosen options are provided.
     */
    public OptionResponse createOptionResponse(List<Option> chosenOptions){
        log.info("Inside OptionResponseService, createOptionResponse method!");
        if(chosenOptions != null && !chosenOptions.isEmpty()){
            OptionResponse optionResponse = new OptionResponse(chosenOptions, authenticationService.getCurrentUserName());
            return optionResponseRepository.save(optionResponse);
        }
        else throwInvalidResourceDetailsException("OptionResponse","No chosenOptions provided!");
        return null;
    }
    /**
     * Retrieves an option response based on the provided option response ID.
     * @param optionResponseId The ID of the option response to retrieve.
     * @return The retrieved option response.
     * @throws ResourceNotFoundException      If no active option response is found.
     * @throws InvalidResourceDetailsException If no option response ID is provided.
     */
    public OptionResponse getOptionResponseById(Long optionResponseId){
        log.info("Inside OptionResponseService, getOptionResponseById method!");
        if(optionResponseId != null) {
            return optionResponseRepository.findByOptionResponseIdAndInActive(optionResponseId, false).orElseThrow(() -> {
                log.error("Option not found!, throwing ResourceNotFoundException");
                throwResourceNotFoundException("OptionResponse", "No active option responses available for with optionResponseId: " + optionResponseId);
                return null;
            });
        } log.error("optionResponseId not given!, throwing InvalidResourceDetailsException");
       throwInvalidResourceDetailsException("OptionResponse","optionResponseId must be provided!");
        return null;
    }
    /**
     * Saves the provided option response.
     * @param optionResponse The option response to save.
     * @throws InvalidResourceDetailsException If no option response is provided.
     */
    public OptionResponse saveOptionResponse(OptionResponse optionResponse){
        if(optionResponse != null){
            log.info("Successfully saved OptionResponse!");
            return optionResponseRepository.save(optionResponse);
        }
        else {
            log.error("Option Response must be provided!, throwing InvalidResourceDetailsException");
            throwInvalidResourceDetailsException("OptionResponse","Option Response must be provided!");
        }
        return null;
    }
}
