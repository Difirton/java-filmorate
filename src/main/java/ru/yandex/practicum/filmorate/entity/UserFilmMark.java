package ru.yandex.practicum.filmorate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFilmMark {
    private Long id;
    private User user;
    private Film film;
    private Integer mark;
}
