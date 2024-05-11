package com.valuelabs.livequiz.controller;

import com.valuelabs.livequiz.model.dto.request.QuestionCreationDTO;
import com.valuelabs.livequiz.model.dto.response.DisplayQuestionDTO;
import com.valuelabs.livequiz.model.dto.response.ErrorStatusDTO;
import com.valuelabs.livequiz.model.entity.Question;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.service.question.DisplayQuestionService;
import com.valuelabs.livequiz.service.question.QuestionService;
import com.valuelabs.livequiz.service.question.UpdateQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for handling questions.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/question")
@Slf4j
public class QuestionController {
    private final QuestionService questionService;
    private final DisplayQuestionService displayQuestionService;
    private final UpdateQuestionService updateQuestionService;
    /**
     * Constructor to inject dependencies.
     */
    @Lazy
    @Autowired
    public QuestionController(QuestionService questionService, DisplayQuestionService displayQuestionService, UpdateQuestionService updateQuestionService) {
        this.questionService = questionService;
        this.displayQuestionService = displayQuestionService;
        this.updateQuestionService = updateQuestionService;
    }
    /**
     * Endpoint to create a new question.
     * @param questionCreationDTO DTO containing question details.
     * @return ResponseEntity with the created question or an error status.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createQuestion(@RequestBody QuestionCreationDTO questionCreationDTO){
        log.info("Inside QuestionController, createQuestion method");
        Question question = questionService.createQuestion(questionCreationDTO);
        log.debug("Question created successfully!" + question.getQuestionText());
            return new ResponseEntity<>(question, HttpStatus.CREATED);
    }
    /**
     * Endpoint to create a new question within a specific quiz.
     * @param quizId             ID of the quiz in which the question will be created.
     * @param questionCreationDTO DTO containing question details.
     * @return ResponseEntity indicating the result of question creation.
     */
    @PostMapping("/createQuestion")
    public ResponseEntity<?> createQuestionInQuiz(@RequestParam Long quizId,@RequestBody QuestionCreationDTO questionCreationDTO){
        log.info("Inside QuestionController, createQuestionInQuiz method");
        questionService.createQuestionInQuiz(quizId,questionCreationDTO);
        log.debug("Question created Successfully in Quiz with " + quizId);
        return new ResponseEntity<>(new ErrorStatusDTO("Question created Successfully in Quiz with " + quizId), HttpStatus.CREATED);
    }
    /**
     * Endpoint to retrieve a question by its ID.
     * @param questionId ID of the question to retrieve.
     * @return ResponseEntity with the retrieved question or an error status.
     */
    @GetMapping()
    public ResponseEntity<?> getQuestionById(@RequestParam Long questionId){
        log.info("Inside QuestionController, getQuestionById method");
        DisplayQuestionDTO question = displayQuestionService.displayQuestion(questionId,false);
        log.debug("Successfully question fetched and ready to display!");
        return new ResponseEntity<>(question, HttpStatus.FOUND);
    }
    /**
     * Endpoint to retrieve all questions.
     * @return ResponseEntity with a list of all questions or an error status.
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllQuestions(@RequestParam Boolean inActive){
        log.info("Inside QuestionController, getAllQuestions method");
        List<DisplayQuestionDTO> allQuestions = displayQuestionService.displayAllQuestions(inActive);
        log.debug("Successfully questions fetched and ready to display!");
        return new ResponseEntity<>(allQuestions, HttpStatus.FOUND);
    }
    /**
     * Endpoint to delete a question by its ID.
     * @param questionId ID of the question to delete.
     * @return ResponseEntity indicating the result of question deletion.
     */
    @DeleteMapping()
    public ResponseEntity<?> deleteQuestion(@RequestParam Long questionId){
        log.info("Inside QuestionController, deleteQuestion method");
        updateQuestionService.deleteQuestion(questionId);
        log.debug("Question deleted successfully!");
        return new ResponseEntity<>(new ErrorStatusDTO("Question deleted successfully!"), HttpStatus.FOUND);
    }
}
