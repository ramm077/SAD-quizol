package com.valuelabs.livequiz.model.dto.request;

import java.util.List;

/**
 * List of Responses for the user for a quiz
 * @param responseList list of responses for the user, will receive List<CaptureResponseDTO>
 * @param finalSubmit whether to finally submit the responses for the user
 */
public record CaptureResponsesDTO(List<CaptureResponseDTO> responseList,Boolean finalSubmit) {
}
