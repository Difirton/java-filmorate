package ru.yandex.practicum.filmorate.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"user", "review"})
public class ReviewRate {
    private Long id;

    @NotNull
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private User user;

    @NotNull
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private Review review;

    @NotNull
    private Boolean isPositive;

    public static ReviewRateBuilder builder() {
        return new ReviewRateBuilder();
    }

    public static class ReviewRateBuilder {
        private Long id;

        @NotNull
        private User user;

        @NotNull
        private Review review;

        @NotNull
        private Boolean isPositive;

        private ReviewRateBuilder() { }

        public ReviewRateBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ReviewRateBuilder user(User user) {
            this.user = user;
            return this;
        }

        public ReviewRateBuilder review(Review review) {
            this.review = review;
            return this;
        }

        public ReviewRateBuilder isPositive(Boolean isPositive) {
            this.isPositive = isPositive;
            return this;
        }

        public ReviewRate build() {
            return new ReviewRate(id, user, review, isPositive);
        }
    }
}
