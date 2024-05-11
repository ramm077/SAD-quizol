package com.valuelabs.livequiz.model.dto.request;

import java.util.List;

/**
 * DTO updating all the responses for a user
 * @param textResponses list of new text responses for text type question
 * @param optionResponses list of new option type responses for text type question
 * @param finalSubmit whether final submit is true or false
 */
public record UpdateResponsesDTO(List<UpdateTextResponseDTO> textResponses, List<UpdateOptionResponseDTO> optionResponses, Boolean finalSubmit) {
}
