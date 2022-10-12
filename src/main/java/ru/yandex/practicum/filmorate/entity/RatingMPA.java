package ru.yandex.practicum.filmorate.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingMPA {
    private Long id;

    @JsonProperty(value = "name")
    private String title;

    public static RatingMPABuilder builder() {
        return new RatingMPABuilder();
    }

    public static class RatingMPABuilder {
        private Long id;
        private String title;

        private RatingMPABuilder() { }

        public RatingMPABuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RatingMPABuilder title(String title) {
            this.title = title;
            return this;
        }

        public RatingMPA build() {
            return new RatingMPA(id, title);
        }
    }
}
