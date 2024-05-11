package com.valuelabs.livequiz.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@Table
@AllArgsConstructor
@NoArgsConstructor
public class OptionResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_response_id",unique = true,nullable = false)
    private Long optionResponseId;
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(
            name = "option_response_chosen_options",
            joinColumns = @JoinColumn(name = "option_response_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<Option> chosenOptions;
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

    public OptionResponse(List<Option> chosenOptions, String createdBy) {
        this.chosenOptions = chosenOptions;
        this.createdBy = createdBy;
        this.inActive = false;
    }

}
