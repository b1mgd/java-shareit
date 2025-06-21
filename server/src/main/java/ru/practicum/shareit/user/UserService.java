package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.PostUserRequest;
import ru.practicum.shareit.user.dto.PatchUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUser(long id);

    UserDto createUser(PostUserRequest request);

    UserDto patchUser(PatchUserRequest request, long userId);

    void deleteUser(long id);
}
