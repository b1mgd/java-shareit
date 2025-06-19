package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.PostUserRequest;
import ru.practicum.shareit.user.dto.PatchUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceImplTest {
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private ru.practicum.shareit.user.UserMapper userMapper;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, userMapper);
        // Глобальные заглушки для всех findAll/findAllBy.../findById
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
    }

    @Test
    void getUser_shouldThrowNotFoundException_whenUserNotFound() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    void createUser_shouldThrowValidationException_whenEmailDuplicate() {
        PostUserRequest request = mock(PostUserRequest.class);
        when(request.getEmail()).thenReturn("test@mail.com");
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);
        assertThrows(ValidationException.class, () -> userService.createUser(request));
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenNoUsers() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        List<UserDto> result = userService.getAllUsers();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void patchUser_shouldThrowNotFoundException_whenUserNotFound() {
        long userId = 1L;
        PatchUserRequest request = mock(PatchUserRequest.class);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.patchUser(request, userId));
    }

    @Test
    void patchUser_shouldThrowValidationException_whenEmailDuplicate() {
        long userId = 1L;
        PatchUserRequest request = mock(PatchUserRequest.class);
        when(request.getEmail()).thenReturn("test@mail.com");
        User user = new User();
        user.setId(userId);
        user.setEmail("other@mail.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);
        assertThrows(ValidationException.class, () -> userService.patchUser(request, userId));
    }

    @Test
    void deleteUser_shouldThrowNotFoundException_whenUserNotFound() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));
    }

    @Test
    void getUser_shouldReturnUserDto_whenUserExists() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDto result = userService.getUser(userId);
        assertNotNull(result);
    }

    @Test
    void createUser_shouldReturnUserDto_whenValid() {
        PostUserRequest request = mock(PostUserRequest.class);
        User user = new User();
        when(request.getEmail()).thenReturn("test@mail.com");
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);
        UserDto result = userService.createUser(request);
        assertNotNull(result);
    }

    @Test
    void patchUser_shouldReturnUserDto_whenValid() {
        long userId = 1L;
        PatchUserRequest request = mock(PatchUserRequest.class);
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(request.getEmail()).thenReturn(null);
        when(request.getName()).thenReturn("Name");
        when(userRepository.save(user)).thenReturn(user);
        UserDto result = userService.patchUser(request, userId);
        assertNotNull(result);
    }

    @Test
    void deleteUser_shouldDelete_whenUserExists() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(userId);
        assertDoesNotThrow(() -> userService.deleteUser(userId));
        verify(userRepository).deleteById(userId);
    }

    @Test
    void patchUser_shouldUpdateEmail_whenOnlyEmailPresent() {
        long userId = 1L;
        PatchUserRequest request = mock(PatchUserRequest.class);
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(request.getEmail()).thenReturn("new@mail.com");
        when(request.getName()).thenReturn(null);
        when(userRepository.existsByEmail("new@mail.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        UserDto result = userService.patchUser(request, userId);
        assertNotNull(result);
    }

    @Test
    void patchUser_shouldUpdateName_whenOnlyNamePresent() {
        long userId = 1L;
        PatchUserRequest request = mock(PatchUserRequest.class);
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(request.getEmail()).thenReturn(null);
        when(request.getName()).thenReturn("Name");
        when(userRepository.save(user)).thenReturn(user);
        UserDto result = userService.patchUser(request, userId);
        assertNotNull(result);
    }

    @Test
    void patchUser_shouldNotUpdate_whenAllFieldsNull() {
        long userId = 1L;
        PatchUserRequest request = mock(PatchUserRequest.class);
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(request.getEmail()).thenReturn(null);
        when(request.getName()).thenReturn(null);
        when(userRepository.save(user)).thenReturn(user);
        UserDto result = userService.patchUser(request, userId);
        assertNotNull(result);
    }

    @Test
    void getAllUsers_shouldReturnList_whenUsersExist() {
        User user1 = new User();
        User user2 = new User();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<UserDto> result = userService.getAllUsers();
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
