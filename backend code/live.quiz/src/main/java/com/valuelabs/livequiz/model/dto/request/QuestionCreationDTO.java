package com.valuelabs.livequiz.model.dto.request;

import com.valuelabs.livequiz.model.enums.QuestionType;

import java.util.List;

/**
 * DTO for creating individual questions
 * @param questionType Type of the question SINGLE,MULTIPLE,TEXT
 * @param questionText the text value of the question
 * @param optionList list of options assigned for the question
 */
public record QuestionCreationDTO(QuestionType questionType, String questionText, List<OptionCreationDTO> optionList) {
}
