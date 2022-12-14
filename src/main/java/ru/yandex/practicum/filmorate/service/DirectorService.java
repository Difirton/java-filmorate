package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.error.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorRepository directorRepository;

    @Transactional
    public Director createDirector(Director director) {
        return directorRepository.save(director);
    }

    public Director getDirectorById(Long id) {
        return directorRepository.findById(id)
                .orElseThrow(() -> new DirectorNotFoundException(id));
    }

    public List<Director> getAllDirectors() {
        return directorRepository.findAll();
    }

    @Transactional
    public Director updateDirector(Long id, Director newDirector) {
        return directorRepository.findById(id)
                .map(d -> {
                    d.setName(newDirector.getName());
                    return directorRepository.update(d);
                })
                .orElseThrow(() -> new DirectorNotFoundException(id));
    }

    public void removeDirectorById(Long id) {
        directorRepository.deleteById(id);
    }
}
