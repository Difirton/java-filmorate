package ru.yandex.practicum.filmorate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Director {
    private Long id;

    @NotBlank(message = "Director's name should not be empty")
    private String name;
}
