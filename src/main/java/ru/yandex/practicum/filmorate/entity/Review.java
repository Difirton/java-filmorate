package ru.yandex.practicum.filmorate.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "usersRates")
@ToString(exclude = "usersRates")
public class Review {
    @JsonProperty(value = "reviewId")
    private Long id;

    @NotBlank
    private String content;

    @NotNull
    @JsonProperty(value = "isPositive")
    private Boolean isPositive;

    @NotNull
    private Long userId;

    @NotNull
    private Long filmId;

    private int useful = 0;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private List<User> usersRates = new ArrayList<>();

    public void addLike(User user) {
        this.usersRates.add(user);
        this.useful++;
    }

    public void addDislike(User user) {
        this.usersRates.add(user);
        this.useful--;
    }

    public void removeLike(User user) {
        this.usersRates.remove(user);
        this.useful--;
    }

    public void removeDislike(User user) {
        this.usersRates.remove(user);
        this.useful++;
    }

    public static ReviewBuilder builder() {
        return new ReviewBuilder();
    }

    public static class ReviewBuilder {
        private Long id;

        @NotBlank
        private String content;

        @NotNull
        private Boolean isPositive;

        @NotNull
        private Long userId;

        @NotNull
        private Long filmId;

        private int useful = 0;

        private List<User> usersRates = new ArrayList<>();

        private ReviewBuilder() { }

        public ReviewBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ReviewBuilder content(String content) {
            this.content = content;
            return this;
        }

        public ReviewBuilder isPositive(Boolean isPositive) {
            this.isPositive = isPositive;
            return this;
        }

        public ReviewBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public ReviewBuilder filmId(Long filmId) {
            this.filmId = filmId;
            return this;
        }

        public ReviewBuilder useful(int useful) {
            this.useful = useful;
            return this;
        }

        public ReviewBuilder usersRates(List<User> usersRates) {
            this.usersRates = usersRates;
            return this;
        }

        public Review build() {
            return new Review(id, content, isPositive, userId, filmId, useful, usersRates);
        }
    }
}
