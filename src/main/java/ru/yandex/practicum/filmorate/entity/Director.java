package ru.yandex.practicum.filmorate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Director {
    private Long id;

    @NotBlank(message = "Director's name should not be empty")
    private String name;

    public static DirectorBuilder builder() {
        return new DirectorBuilder();
    }

    public static class DirectorBuilder {
        private Long id;
        @NotBlank(message = "Director's name should not be empty")
        private String name;

        private DirectorBuilder() { }

        public DirectorBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DirectorBuilder name(String name) {
            this.name = name;
            return this;
        }

        public Director build() {
            return new Director(id, name);
        }
    }
}
