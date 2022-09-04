package ru.yandex.practicum.filmorate.repository;

import java.util.List;
import java.util.Optional;

public interface StandardCRUDRepository<M> {

    M save(M m);

    M update(M m);

    int deleteById(Long id);

    List<M> findAll();

    Optional<M> findById(Long id);

    int[] saveAll(List<M> m);

}
