package com.valuelabs.livequiz.model.entity;

import com.valuelabs.livequiz.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id",unique = true,nullable = false)
    private Long userId;
    @Column(name = "first_name", nullable = false, length = 30)
    private String firstName;
    @Column(name = "last_name", length = 30)
    private String lastName;
    @Column(nullable = false, unique = true, length = 60)
    private String emailId;
    @Column(name = "password",nullable = false, length = 100)
    private String password;
    @Column(name = "phone_number",length = 15)
    private String phoneNumber;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;
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
    public User(String firstName, String lastName,String emailId,String password, String phoneNumber,String createdBy) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailId = emailId;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.createdBy = createdBy;
        this.role = Role.RESPONDER;
        this.inActive = false;
    }
    public User(String firstName, String lastName,String emailId,String password, String phoneNumber,String createdBy,Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailId = emailId;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.createdBy = createdBy;
        this.role = role;
        this.inActive = false;
    }
    public String getUserName(){
        return (this.firstName + this.lastName);
    }
    public User(){
        this.role = Role.RESPONDER;
        this.inActive = false;
    }
}
