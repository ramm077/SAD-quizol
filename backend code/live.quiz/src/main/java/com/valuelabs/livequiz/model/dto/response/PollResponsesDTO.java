package com.valuelabs.livequiz.model.dto.response;

import java.util.List;

public record PollResponsesDTO(String quizName, Long questionId, String questionText, List<OptionDTO> optionList,Integer totalResponses) {
}
