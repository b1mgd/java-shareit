package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.PatchUserRequest;
import ru.practicum.shareit.user.dto.PostUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserControllerImpl implements UserController {
    private final UserClient userClient;

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers() {
        log.info("Запрос на вывод всех пользователей");
        return userClient.getAllUsers();
    }

    @Override
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUser(@PathVariable long id) {
        log.info("Запрос на получение пользователя с id: {}", id);
        return userClient.getUser(id);
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody PostUserRequest request) {
        log.info("Запрос на добавление пользователя: {}", request);
        return userClient.createUser(request);
    }

    @Override
    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto patchUser(@RequestBody PatchUserRequest request,
                             @PathVariable long userId) {
        log.info("Запрос на изменение пользователя с userId: {}. Request: {}", userId, request);
        return userClient.patchUser(request, userId);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long id) {
        log.info("Запрос на удаление пользователя с id: {}", id);
        userClient.deleteUser(id);
    }
}
