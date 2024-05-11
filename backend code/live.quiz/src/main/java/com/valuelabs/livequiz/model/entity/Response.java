package com.valuelabs.livequiz.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "response")
@Data
@AllArgsConstructor
public class Response {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id",unique = true,nullable = false)
    private Long responseId;
    @ManyToOne(cascade = CascadeType.ALL,fetch =FetchType.EAGER)
    @JoinColumn(name = "question_id",referencedColumnName = "question_id")
    private Question question;
    @OneToOne
    private TextResponse textResponse;
    @OneToOne
    private OptionResponse optionResponse;
    @ManyToOne(cascade = CascadeType.ALL,fetch =FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @JsonIgnore
    private User user;
    @ManyToOne(cascade = CascadeType.ALL,fetch =FetchType.EAGER)
    @JoinColumn(name = "scheduler_id",referencedColumnName = "scheduler_id")
    @JsonIgnore
    private Scheduler scheduler;
    @Column
    private Integer scoreCount;
    @Column
    @CreationTimestamp
    private Timestamp createdResponseTime;
    @Column
    @UpdateTimestamp
    private Timestamp updatedResponseTime;
    @Column
    private Boolean inActive;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column
    private Boolean isAttempted;
    @Column
    private Boolean finalSubmit;

    public Response(){
        this.inActive = false;
        this.isAttempted = false;
        this.finalSubmit = false;
    }
    public Response(User user, Scheduler scheduler, Question question){
        this.user = user;
        this.scheduler = scheduler;
        this.question = question;
        this.inActive = false;
        this.scoreCount = -1;
        this.isAttempted = false;
        this.finalSubmit = false;
    }

}
