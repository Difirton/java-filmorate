package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User newUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<User> findAllUsers() {
        return userService.getAllUsers();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public User updateUser(@PathVariable("id") Long id,
                                 @Valid @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public User findOneUser(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        userService.removeUserById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public User updateUserRootMapping(@Valid @RequestBody User user) {
        return userService.updateUser(user.getId(), user);
    }
}
