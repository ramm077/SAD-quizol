package com.valuelabs.livequiz.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@Table
@AllArgsConstructor
public class TextResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long textResponseId;
    @Column(name = "answer_text",length = 200)
    private String answerText;
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
    public TextResponse(){
        this.inActive = false;
    }
    public TextResponse(String answerText, String createdBy){
        this.answerText = answerText;
        this.createdBy = createdBy;
        this.inActive = false;
    }
}
