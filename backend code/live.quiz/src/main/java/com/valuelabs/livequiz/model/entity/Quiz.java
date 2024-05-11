package com.valuelabs.livequiz.model.entity;

import com.valuelabs.livequiz.model.enums.QuizType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "quiz")
@Data
@AllArgsConstructor
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id",unique = true,nullable = false)
    private Long quizId;
    @Column(name = "quiz_name",unique = true,nullable = false)
    private String quizName;
    @Enumerated(EnumType.STRING)
    private QuizType quizType;
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(
            name = "quiz_questions",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questionList;
    @Column(name = "in_active",nullable = false)
    private Boolean inActive;
    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "created_by")
    private String createdBy;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @Column(name = "updated_by")
    private String updatedBy;
    private String imagePath;
    public Quiz(){
        this.inActive = false;
    }
    public Quiz(String quizName, QuizType quizType,List<Question> questionList,String createdBy){
        this.quizName = quizName;
        this.quizType = quizType;
        this.questionList =questionList;
        this.createdBy =createdBy;
        this.inActive = false;
    }
    public Quiz(String quizName, QuizType quizType){
        this.quizName = quizName;
        this.quizType = quizType;
        this.inActive = false;
    }
    public Quiz(String quizName, QuizType quizType, String createdBy){
        this.quizName = quizName;
        this.quizType = quizType;
        this.inActive = false;
        this.createdBy = createdBy;
    }

    public Quiz(Long quizId, String quizName, QuizType quizType, List<Question> questionList) {
        this.quizId = quizId;
        this.quizName = quizName;
        this.quizType = quizType;
        this.questionList = questionList;
        this.inActive = false;
    }
}
