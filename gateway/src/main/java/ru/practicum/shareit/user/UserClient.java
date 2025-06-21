package ru.practicum.shareit.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.PatchUserRequest;
import ru.practicum.shareit.user.dto.PostUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(serverUrl, builder, API_PREFIX);
    }

    public List<UserDto> getAllUsers() {
        ResponseEntity<List<UserDto>> response = getList("", UserDto.class);
        return response.getBody();
    }

    public UserDto getUser(long id) {
        ResponseEntity<UserDto> response = get("/" + id, UserDto.class);
        return response.getBody();
    }

    public UserDto createUser(PostUserRequest request) {
        ResponseEntity<UserDto> response = post("", request, UserDto.class);
        return response.getBody();
    }

    public UserDto patchUser(PatchUserRequest request, long userId) {
        ResponseEntity<UserDto> response = patch("/" + userId, request, UserDto.class);
        return response.getBody();
    }

    public void deleteUser(long id) {
        delete("/" + id);
    }
}
