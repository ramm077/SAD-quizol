package com.valuelabs.livequiz.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "scheduler")
@Data
@AllArgsConstructor
public class Scheduler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduler_id")
    private Long schedulerId;
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "quiz_id",referencedColumnName = "quiz_id")
    private Quiz quiz;
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(
            name = "scheduler_user_list",
            joinColumns = @JoinColumn(name = "scheduler_id"),
            inverseJoinColumns = @JoinColumn(name = "user_ids")
    )
    private List<User> userList;
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
    @Column
    private Timestamp startTime;
    @Column
    private Timestamp endTime;
    @Column
    private String reason;
    @Column
    private Boolean inActive;

    public Scheduler(){
        this.inActive = false;
    }

    public Scheduler(Quiz quiz, String createdBy, Timestamp startTime, Timestamp endTime) {
        this.quiz = quiz;
        this.createdBy = createdBy;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public Scheduler(Timestamp startTime,Timestamp endTime,Quiz quiz){
        this.startTime=startTime;
        this.endTime=endTime;
        this.quiz=quiz;
        this.inActive = false;
    }
}
