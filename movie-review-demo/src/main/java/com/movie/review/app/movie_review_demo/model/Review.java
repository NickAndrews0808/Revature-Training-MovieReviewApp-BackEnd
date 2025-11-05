package com.movie.review.app.movie_review_demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // Many reviews can belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String username;


    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 2000)
    private String review; // review text

    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "update_on")
    private LocalDateTime updatedOn;

    @Column(name = "update_by")
    private String updatedBy;

    // Automatically set timestamps
    @PrePersist
    protected void onCreate() {
        this.createdOn = LocalDateTime.now();
        if (this.createdBy == null) this.createdBy = "system";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedOn = LocalDateTime.now();
        if (this.updatedBy == null) this.updatedBy = "system";
    }

    // ✅ Convenience getters for frontend / controller
    public String getReviewText() {
        return review;
    }

    public LocalDateTime getCreatedAt() {
        return createdOn;
    }

    public String getUsername() {
        return user != null ? user.getUsername() : "Anonymous";
    }
}
