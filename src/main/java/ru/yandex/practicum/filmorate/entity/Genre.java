package ru.yandex.practicum.filmorate.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"title", "id"})
@ToString(exclude = "films")
@Builder
public class Genre {
    private Long id;

    @NotBlank
    @JsonProperty(value = "name")
    private String title;

    @Builder.Default
    private List<Film> films = new ArrayList<>();
}
