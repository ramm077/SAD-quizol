package com.valuelabs.livequiz.model.dto.request;

/**
 * DTO for updating individual text response
 * @param responseId id of individual response
 * @param textResponseId id of the text response to updated
 * @param answerText new answer text to be updated with
 */
public record UpdateTextResponseDTO(Long responseId,Long textResponseId,String answerText) {
}
