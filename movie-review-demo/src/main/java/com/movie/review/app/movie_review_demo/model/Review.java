package com.movie.review.app.movie_review_demo.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

//Review Entity
@Entity
@Table(name = "user_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Review{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long movieId;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 2000)
    private String review;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdOn;

    private String updateBy;

    private LocalDateTime updateOn;

    @PrePersist
    protected void onCreate(){
        this.createdBy = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        this.updateOn = LocalDateTime.now();
    }
}