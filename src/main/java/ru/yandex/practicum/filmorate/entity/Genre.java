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
public class Genre {
    private Long id;

    @NotBlank
    @JsonProperty(value = "name")
    private String title;

    private List<Film> films = new ArrayList<>();

    public static GenreBuilder builder() {
        return new GenreBuilder();
    }

    public static class GenreBuilder {
        private Long id;

        @NotBlank
        private String title;

        private List<Film> films;

        private GenreBuilder() { }

        public GenreBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public GenreBuilder title(String title) {
            this.title = title;
            return this;
        }

        public GenreBuilder films(List<Film> films) {
            this.films = films;
            return this;
        }

        public Genre build() {
            return new Genre(id, title, films);
        }
    }
}
