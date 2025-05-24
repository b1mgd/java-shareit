package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import ru.practicum.shareit.user.dto.PostUserRequest;
import ru.practicum.shareit.user.dto.PatchUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserController {
    List<UserDto> getAllUsers();

    UserDto getUser(@Positive long id);

    UserDto createUser(@Valid PostUserRequest request);

    UserDto patchUser(@Valid PatchUserRequest request, @Positive long userId);

    void deleteUser(@Positive long id);
}
