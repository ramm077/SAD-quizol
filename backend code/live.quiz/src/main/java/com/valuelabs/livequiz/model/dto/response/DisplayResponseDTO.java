package com.valuelabs.livequiz.model.dto.response;

/**
 * DTO for displaying individual response of  each option or text based response
 * @param responseId id of the response
 * @param textResponse DTO for the text response
 * @param optionResponses DTO for option based response
 */
public record DisplayResponseDTO(Long responseId,TextResponseDTO textResponse, OptionResponseDTO optionResponses) {
}
