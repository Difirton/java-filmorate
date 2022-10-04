package ru.yandex.practicum.filmorate.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "usersRates")
@ToString(exclude = "usersRates")
@Builder
public class Review {
    @JsonProperty(value = "reviewId")
    private Long id;

    @NotNull
    private String content;

    @NotNull
    @JsonProperty(value = "isPositive")
    private Boolean isPositive;

    @NotNull
    private Long userId;

    @NotNull
    private Long filmId;

    @Builder.Default
    private int useful = 0;

    @Builder.Default
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private List<User> usersRates = new ArrayList<>();
}
