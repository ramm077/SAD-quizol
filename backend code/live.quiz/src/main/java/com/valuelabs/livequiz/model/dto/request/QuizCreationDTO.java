package com.valuelabs.livequiz.model.dto.request;

import com.valuelabs.livequiz.model.enums.QuizType;

import java.util.List;

/**
 * DTO for creating individual quiz
 * @param quizName name of the quiz
 * @param quizType type of the quiz -> TEST,OPEN_ENDED,POLL,OTHERS
 * @param questionIds list of existing question ids for the quiz
 * @param questionList list newly created question while creating quiz
 * @param quizImageBase64URL Image of the quiz cards.
 */
public record QuizCreationDTO(String quizName, QuizType quizType, List<Long> questionIds, List<QuestionCreationDTO> questionList,byte[] quizImageBase64URL) {

}
