package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.PostUserRequest;
import ru.practicum.shareit.user.dto.PatchUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        List<UserDto> result = userRepository.findAll()
                .stream()
                .map(userMapper::mapToUserDto)
                .collect(Collectors.toList());
        log.info("Получен результат: {}", result);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(long id) {
        UserDto result = userRepository.findById(id)
                .map(userMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не был найден"));
        log.info("Получен результат: {}", result);
        return result;
    }

    @Override
    public UserDto createUser(PostUserRequest request) {
        String email = request.getEmail();

        if (checkContainsDuplicateEmail(email)) {
            throw new ValidationException("Пользователь с email: " + email + " уже зарегистрирован");
        }

        User user = userRepository.save(userMapper.mapToUser(request));
        log.info("Получен результат: {}", user);

        return userMapper.mapToUserDto(user);
    }

    @Override
    public UserDto patchUser(PatchUserRequest request, long userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не был найден"));
        String email = request.getEmail();
        String name = request.getName();

        if (email != null
                && checkContainsDuplicateEmail(email)
                && !existingUser.getEmail().equals(email)) {
            throw new ValidationException("Пользователь с email: " + email + " уже зарегистрирован");
        }

        if (email != null) {
            existingUser.setEmail(email);
        }

        if (name != null) {
            existingUser.setName(name);
        }

        User user = userRepository.save(existingUser);
        log.info("Получен результат: {}", user);

        return userMapper.mapToUserDto(user);
    }

    @Override
    public void deleteUser(long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id " + id + " не был найден");
        }

        userRepository.deleteById(id);
        log.info("Пользователь с id {} был удален", id);
    }

    private boolean checkContainsDuplicateEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
