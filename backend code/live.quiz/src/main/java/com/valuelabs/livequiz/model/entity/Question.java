package com.valuelabs.livequiz.model.entity;

import com.valuelabs.livequiz.model.enums.QuestionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "question")
@Data
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id",unique = true,nullable = false)
    private Long questionId;
    @Column(name = "question_text",nullable = false)
    private String questionText;
    @Enumerated(EnumType.STRING)
    @Column
    private QuestionType questionType;
    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinTable(
            name = "question_option_list",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<Option> optionList;
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
    private Boolean inActive;
    public Question(){
        this.inActive = false;
    }
    public Question(QuestionType questionType, String questionText, List<Option> optionList, String createdBy){
        this.questionType = questionType;
        this.questionText = questionText;
        this.optionList = optionList;
        this.createdBy = createdBy;
        this.inActive = false;
    }
    public Question(QuestionType questionType,String questionText){
        this.questionType = questionType;
        this.questionText = questionText;
        this.inActive = false;
    }
    public Question(QuestionType questionType,String questionText,String createdBy){
        this.questionType = questionType;
        this.questionText = questionText;
        this.inActive = false;
        this.createdBy = createdBy;
    }

}
