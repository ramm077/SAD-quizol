package com.valuelabs.livequiz.service.option;

import com.valuelabs.livequiz.model.dto.request.OptionCreationDTO;
import com.valuelabs.livequiz.model.entity.Option;
import com.valuelabs.livequiz.model.entity.Question;
import com.valuelabs.livequiz.repository.OptionRepository;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.valuelabs.livequiz.utils.InputValidator.validateDTO;
/**
 * Service class for managing options.
 * @Service
 */
@Service
@Slf4j
public class OptionService {
    private final OptionRepository optionRepository;
    private final AuthenticationService authenticationService;
    /**
     * Constructs an OptionService with the specified OptionRepository.
     * @param optionRepository      The repository for handling options.
     * @param authenticationService used to fetch the username from the security context.
     */
    @Lazy
    @Autowired
    public OptionService(OptionRepository optionRepository, AuthenticationService authenticationService) {
        this.optionRepository = optionRepository;
        this.authenticationService = authenticationService;
    }
    /**
     * Creates an Option based on the provided OptionCreationDTO.
     * @param optionDTO The data transfer object containing option details.
     * Validates the OptionCreationDTO using a utility method ,where no field should not be null
     * @return The created Option.
     */
    public Option createOption(OptionCreationDTO optionDTO) {
        log.info("Inside OptionService, createOption method!");
        log.info("validating all the fields inside the DTO record!");
        validateDTO(optionDTO);
        log.info("OptionDTO validated successfully, saving the option inside the Database!");
        return optionRepository.save(new Option(optionDTO.optionText(), optionDTO.isTrue(), authenticationService.getCurrentUserName()));
    }
    /**
     * Sets options to a specific Question.
     * @param question    The Question to which options will be associated.
     * @param optionList  List of options to set for the question.
     */
    public void setOptionsToQuestion(Question question, List<Option> optionList){
        log.info("Inside OptionService, setOptionsToQuestion method!");
        optionList.forEach(option -> option.setQuestion(question));
        log.debug("Successfully set all the options given to the question with id: "+question.getQuestionId());
    }
}
