package com.valuelabs.livequiz.model.dto.response;

/**
 * DTO for displaying individual Text based response
 * @param textResponseId id of text based response
 * @param answerText text value of the text based response
 */
public record TextResponseDTO(Long textResponseId,String answerText) {
}
