package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.PostUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void mapToUserDto_shouldMapAllFields() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@email.com");

        UserDto dto = userMapper.mapToUserDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void mapToUserDto_shouldReturnNull_whenUserIsNull() {
        assertThat(userMapper.mapToUserDto(null)).isNull();
    }

    @Test
    void mapToUser_shouldMapAllFields() {
        PostUserRequest req = new PostUserRequest();
        req.setName("User");
        req.setEmail("user@email.com");

        User user = userMapper.mapToUser(req);

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("User");
        assertThat(user.getEmail()).isEqualTo("user@email.com");
    }

    @Test
    void mapToUser_shouldReturnNull_whenRequestIsNull() {
        assertThat(userMapper.mapToUser(null)).isNull();
    }
}
