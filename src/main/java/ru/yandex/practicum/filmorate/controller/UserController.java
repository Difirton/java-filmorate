package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final FilmService filmService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User newUser(@Valid @RequestBody User user) {
        log.info("Request to create new user: " + user.toString());
        return userService.createUser(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<User> findAllUsers() {
        return userService.getAllUsers();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public User updateUser(@PathVariable("id") Long id, @Valid @RequestBody User user) {
        log.info("Request to update user with id = {}, parameters to update: {}", id ,user.toString());
        return userService.updateUser(id, user);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public User findUser(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        log.info("Request to delete user with {}", id);
        userService.removeUserById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public User updateUserRootMapping(@Valid @RequestBody User user) {
        log.info("Request to update user with id = {}, parameters to update: {}", user.getId() ,user);
        return userService.updateUser(user.getId(), user);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        log.info("Request to add a user with id = {} as a friend to a user with id ={}", id ,friendId);
        return userService.addFriend(id, friendId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        log.info("Request to remove user with id = {} from friends of user with id = {}", friendId, id);
        userService.removeFriend(id, friendId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}/friends")
    public List<User> getUsersFriends(@PathVariable("id") Long id) {
        return userService.getUserFriends(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonUsersFriends(@PathVariable("id") Long id, @PathVariable("otherId") Long otherId) {
        return userService.getCommonUsersFriends(id, otherId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}/recommendations")
    public List<Film> getRecommendationsForUser(@PathVariable("id") Long userID) {
        return filmService.getRecommendationsForUser(userID);
    }
}
