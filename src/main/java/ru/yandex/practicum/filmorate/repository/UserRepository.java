package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.User;

import java.util.List;

public interface UserRepository extends StandardCRUDRepository<User> {

    int[][] updateAll(List<User> users);

    List<User> findAllFriendsUser(Long id);

    List<User> findCommonUsersFriends(Long id, Long otherId);
}