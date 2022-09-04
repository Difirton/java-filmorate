package ru.yandex.practicum.filmorate.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"user", "friend"})
@Builder
public class UserFriend {
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
    private User friend;

    private boolean approved;
}
