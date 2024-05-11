package com.valuelabs.livequiz.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "option")
@Data
@AllArgsConstructor
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id",unique = true,nullable = false)
    private Long optionId;
    @Column(name = "option_text",nullable = false)
    private String optionText;
    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "in_active")
    private Boolean inActive;
    @Column(name = "is_true")
    private Boolean isTrue;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "question_id")
    @ToString.Exclude
    private Question question;

    public Option(){
        this.inActive = false;
    }
    public Option(String optionText, Boolean isTrue, String createdBy){
        this.inActive = false;
        this.optionText = optionText;
        this.isTrue = isTrue;
        this.createdBy = createdBy;
    }
    public Option(Long optionId,String optionText, Boolean isTrue, String createdBy){
        this.optionId = optionId;
        this.inActive = false;
        this.optionText = optionText;
        this.isTrue = isTrue;
        this.createdBy = createdBy;
    }

//    public Option(Long optionId, String optionText, Timestamp createdAt, String createdBy, Timestamp updatedAt, String updatedBy, Boolean inActive, Boolean isTrue, Question question) {
//        this.optionId = optionId;
//        this.optionText = optionText;
//        this.createdAt = createdAt;
//        this.createdBy = createdBy;
//        this.updatedAt = updatedAt;
//        this.updatedBy = updatedBy;
//        this.inActive = inActive;
//        this.isTrue = isTrue;
//        this.question = question;
//    }
}
