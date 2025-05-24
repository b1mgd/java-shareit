package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> getAllUsers();

    Optional<User> getUser(long id);

    User saveUser(User user);

    User patchUser(User user);

    void deleteUser(long id);
}
