package com.valuelabs.livequiz.controller;

import com.valuelabs.livequiz.model.dto.request.QuizCreationDTO;
import com.valuelabs.livequiz.model.dto.request.QuizMetaDataDTO;
import com.valuelabs.livequiz.model.dto.response.DisplayQuizDTO;
import com.valuelabs.livequiz.model.dto.response.ErrorStatusDTO;
import com.valuelabs.livequiz.model.dto.response.QuizDisplayDTO;
import com.valuelabs.livequiz.model.entity.Quiz;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.service.question.QuestionService;
import com.valuelabs.livequiz.service.quiz.DisplayQuizService;
import com.valuelabs.livequiz.service.quiz.QuizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.valuelabs.livequiz.model.enums.QuizType.TEST;
/**
 * Controller responsible for handling quizzes.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/quiz")
@Slf4j
public class QuizController {
    private final QuizService quizService;
    private final DisplayQuizService displayQuizService;
    private final QuestionService questionService;
    /**
     * Constructor to inject dependencies.
     */
    @Lazy
    @Autowired
    public QuizController(QuizService quizService, DisplayQuizService displayQuizService, QuestionService questionService) {
        this.quizService = quizService;
        this.displayQuizService = displayQuizService;
        this.questionService = questionService;
    }
    /**
     * Endpoint to create a new quiz.
     * @param quizDTO DTO containing quiz details.
     * @return ResponseEntity with the created quiz or an error status.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createQuiz(@RequestBody QuizCreationDTO quizDTO){
        log.info("Inside QuizController, createQuiz method!");
        Quiz quiz = quizService.createQuiz(quizDTO);
        log.debug("Created Quiz successfully! " + quiz.getQuizName());
        return new ResponseEntity<>(quiz, HttpStatus.CREATED);
    }
    /**
     * Endpoint to retrieve a quiz by its ID.
     * @param quizId ID of the quiz to retrieve.
     * @return ResponseEntity with the retrieved quiz or an error status.
     */
    @GetMapping()
    public ResponseEntity<?> getQuizById(@RequestParam Long quizId){
        log.info("Inside QuizController, getQuizById method!");
        DisplayQuizDTO quiz = displayQuizService.displayQuiz(quizId,false);
        log.debug("Successfully quiz"+ quiz.quizName() + " fetched and ready to display!");
        return new ResponseEntity<>(quiz, HttpStatus.FOUND);
    }
    /**
     * Endpoint to retrieve all quizzes.
     * @return ResponseEntity with a list of all quizzes or an error status.
     */
    @GetMapping("/allTypes")
    public ResponseEntity<?> getAllQuizzes(){
        log.info("Inside QuizController, getAllQuizzes method!");
        List<QuizDisplayDTO> quizzes = displayQuizService.displayAllQuizzes(false);
        log.debug("Successfully fetched all types of quizzes!");
        return new ResponseEntity<>(quizzes, HttpStatus.FOUND);
    }
    /**
     * Endpoint to add questions to a quiz.
     * @param quizId      ID of the quiz to which questions will be added.
     * @param questionIds List of question IDs to add to the quiz.
     * @return ResponseEntity indicating the result of adding questions.
     */
    @PostMapping("/addQuestions")
    public ResponseEntity<?> addQuestionsInQuiz(@RequestParam Long quizId, @RequestBody List<Long> questionIds){
        log.info("Inside QuizController, addQuestionsInQuiz method!");
        questionService.addQuestionsInQuiz(quizId,questionIds);
        log.debug("Successfully added questions with questionIds : " + questionIds+ " in quiz with quizId : " + quizId);
        return new ResponseEntity<>(new ErrorStatusDTO("Questions added successfully!"), HttpStatus.CREATED);
    }
    /**
     * Endpoint to delete a question from a quiz.
     * @param quizId     ID of the quiz from which the question will be deleted.
     * @param questionId ID of the question to delete from the quiz.
     * @return ResponseEntity indicating the result of deleting a question from the quiz.
     */
    @DeleteMapping("/delete/question")
    public ResponseEntity<?> deleteQuestionInQuiz(@RequestParam Long quizId, @RequestParam Long questionId){
        log.info("Inside QuizController, deleteQuestionInQuiz method!");
        questionService.deleteQuestionInQuiz(quizId,questionId);
        log.debug("Successfully deleted question with questionId : " + questionId+ " in quiz with quizId : " + quizId);
        return new ResponseEntity<>(new ErrorStatusDTO("Question deleted successfully in Quiz!"), HttpStatus.CREATED);
    }
    /**
     * Endpoint to update quiz metadata.
     * @param quizId           ID of the quiz to update.
     * @param quizMetaDataDTO DTO containing updated quiz metadata.
     * @return ResponseEntity indicating the result of updating quiz metadata.
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateQuiz(@RequestParam Long quizId,@RequestBody QuizMetaDataDTO quizMetaDataDTO){
        log.info("Inside QuizController, updateQuiz method!");
        Quiz quiz = quizService.updateQuiz(quizId,quizMetaDataDTO);
        log.debug("Successfully updated the quiz with quizId : " + quizId);
        return new ResponseEntity<>(quiz, HttpStatus.OK);
    }
    /**
     * Endpoint to delete quiz metadata.
     * @param quizId           ID of the quiz to delete.
     * @return ResponseEntity indicating the result of soft-deleting quiz.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteQuiz(@RequestParam Long quizId){
        quizService.deleteQuizById(quizId);
        return new ResponseEntity<>("Successfully deleted Quiz", HttpStatus.OK);
    }
}