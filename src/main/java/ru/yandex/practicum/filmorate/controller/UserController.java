package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name="The user API", description="API for interacting with endpoints associated with user")
public class UserController {
    private final UserService userService;
    private final FilmService filmService;

    @Operation(summary = "Creates a new user", tags = "user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "The user was created",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @Operation(summary = "Get all users", tags = "user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the users",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<User> findAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Update the user by his id, which is specified in URL", tags = "user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The user was updated",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public User updateUser(@PathVariable("id") @Parameter(description = "The user ID") Long id,
                           @Valid @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @Operation(summary = "Get the user by his id, which is specified in URL", tags = "user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the requested user",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public User findUser(@PathVariable("id") @Parameter(description = "The user ID") Long id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Removes the user by his id, which is specified in URL", tags = "user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The user was removed",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") @Parameter(description = "The user ID") Long id) {
        userService.removeUserById(id);
    }

    @Operation(summary = "Update the user by his id, which is specified in the json body", tags = "user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The user was updated",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user.getId(), user);
    }

    @Operation(summary = "Adds one user as friend to another user's friends list", tags = "user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The user with friends",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") @Parameter(description = "The user ID") Long id,
                          @PathVariable("friendId") @Parameter(description = "The user ID") Long friendId) {
        return userService.addFriend(id, friendId);
    }

    @Operation(summary = "Removes one user from another user's friends list", tags = "user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The user without friends",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") @Parameter(description = "The user ID") Long id,
                             @PathVariable("friendId") @Parameter(description = "The another user ID") Long friendId) {
        userService.removeFriend(id, friendId);
    }

    @Operation(summary = "Get user's friends list", tags = "user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The user's friends list",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}/friends")
    public List<User> getUsersFriends(@PathVariable("id") @Parameter(description = "The user ID") Long id) {
        return userService.getUserFriends(id);
    }

    @Operation(summary = "Get a list of mutual friends of two users", tags = "user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Get a list of mutual friends",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonUsersFriends(
            @PathVariable("id") @Parameter(description = "The user ID") Long id,
            @PathVariable("otherId") @Parameter(description = "The another user ID") Long otherId) {
        return userService.getCommonUsersFriends(id, otherId);
    }

    @Operation(summary = "Get a list of recommended movies to watch", tags = {"user", "movie"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Get a list of recommended movies",
                    content = {
                            @Content(mediaType = "application/json")
                    })
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("{id}/recommendations")
    public List<Film> getRecommendationsForUser(@PathVariable("id") @Parameter(description = "The user ID") Long userID) {
        return filmService.getRecommendationsForUser(userID);
    }
}
