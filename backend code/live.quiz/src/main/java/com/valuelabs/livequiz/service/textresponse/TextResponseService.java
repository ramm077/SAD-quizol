package com.valuelabs.livequiz.service.textresponse;
import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.entity.TextResponse;
import com.valuelabs.livequiz.repository.TextResponseRepository;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static com.valuelabs.livequiz.exception.ExceptionUtility.throwInvalidResourceDetailsException;
import static com.valuelabs.livequiz.exception.ExceptionUtility.throwResourceNotFoundException;
/**
 * The TextResponseService class provides methods for managing text responses, including creating, retrieving, and saving
 * text responses.
 */
@Slf4j
@Service
public class TextResponseService {
    private final TextResponseRepository textResponseRepository;
    private final AuthenticationService authenticationService;
    @Autowired
    public TextResponseService(TextResponseRepository textResponseRepository, AuthenticationService authenticationService) {
        this.textResponseRepository = textResponseRepository;
        this.authenticationService = authenticationService;
    }
    /**
     * Creates a new text response with the provided answer text.
     * @param answerText The answer text for the text response.
     * @return The newly created text response.
     * @throws InvalidResourceDetailsException If no answer text is provided.
     */
    public TextResponse createTextResponse(String answerText){
        log.info("Inside TextResponseService, createTextResponse method!");
        if(answerText != null && !answerText.isEmpty()){
            TextResponse textResponse = new TextResponse(answerText,authenticationService.getCurrentUserName());
            log.debug("Created a new text response with ID: {}", textResponse.getTextResponseId());
            return textResponseRepository.save(textResponse);
        }log.error("Failed to create a text response. No answerText provided!");
        throwInvalidResourceDetailsException("TextResponse","No answerText provided!");
        return null;
    }
    /**
     * Retrieves a text response based on the provided text response ID.
     * @param textResponseId The ID of the text response to retrieve.
     * @return The retrieved text response.
     * @throws ResourceNotFoundException         If no text response is available for the provided ID.
     * @throws InvalidResourceDetailsException    If no text response ID is provided.
     */
    public TextResponse getTextResponseById(Long textResponseId){
        log.info("Inside TextResponseService, getTextResponseById method!");
        if(textResponseId != null){
            return textResponseRepository.findByTextResponseIdAndInActive(textResponseId,false).orElseThrow(() -> { log.warn("No text response available for textResponseId: {}", textResponseId);throwResourceNotFoundException("TextResponse","No text response available for with textResponseId: "+textResponseId); return null;});
        }log.error("Failed to retrieve text response. textResponseId must be provided!");
        throwInvalidResourceDetailsException("TextResponse","textResponseId must be provided!");
        return null;
    }
    /**
     * Saves a text response to the repository.
     * @param textResponse The text response to be saved.
     */
    public TextResponse saveTextResponse(TextResponse textResponse){
        log.info("Inside TextResponseService, saveTextResponse method!");
        if(textResponse != null){
            log.debug("Saved text response with ID: {}", textResponse.getTextResponseId());
            return textResponseRepository.save(textResponse);
        }
        return null;
    }
}
