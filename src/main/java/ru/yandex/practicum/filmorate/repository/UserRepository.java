package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    int count();

    User save(User user);

    User update(User user);

    int deleteById(Long id);

    List<User> findAll();

    Optional<User> findById(Long id);

    int[] saveAll(List<User> user);

    int[][] updateAll(List<User> users);

    List<User> findAllFriendsUser(Long id);

    List<User> findCommonUsersFriends(Long id, Long otherId);
}