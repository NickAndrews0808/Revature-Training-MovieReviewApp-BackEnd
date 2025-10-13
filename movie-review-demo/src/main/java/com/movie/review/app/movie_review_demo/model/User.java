package com.movie.review.app.movie_review_demo.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data // Getter Setter, toString, EqualsAndHashCode, RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Size(min=4, max=20)
    private String name;

    @Email(message = "Email should be valid")
    private String email;

    private List<Review> reviews;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
