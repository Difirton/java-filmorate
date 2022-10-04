package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.binding.FilmGenre;

import java.util.List;

public interface FilmGenreRepository {
    List<FilmGenre> findAll();
}
