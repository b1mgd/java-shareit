package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.dto.PatchUserRequest;
import ru.practicum.shareit.user.dto.PostUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder builder;

    private UserClient userClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(builder.uriTemplateHandler(any())).thenReturn(builder);
        when(builder.requestFactory(any(java.util.function.Supplier.class))).thenReturn(builder);
        when(builder.build()).thenReturn(restTemplate);
        userClient = new UserClient("http://localhost", builder);
    }

    @Test
    void getAllUsers_success() {
        UserDto user = new UserDto(1L, "name", "email");
        List<UserDto> expected = List.of(user);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), any(org.springframework.core.ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity(expected, HttpStatus.OK));
        List<UserDto> result = userClient.getAllUsers();
        assertEquals(expected, result);
    }

    @Test
    void getUser_success() {
        UserDto expected = new UserDto(1L, "name", "email");
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), eq(UserDto.class)))
                .thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));
        UserDto result = userClient.getUser(1L);
        assertEquals(expected, result);
    }

    @Test
    void createUser_success() {
        PostUserRequest req = new PostUserRequest();
        UserDto expected = new UserDto(1L, "name", "email");
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.POST), any(), eq(UserDto.class)))
                .thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));
        UserDto result = userClient.createUser(req);
        assertEquals(expected, result);
    }

    @Test
    void patchUser_success() {
        PatchUserRequest req = new PatchUserRequest();
        UserDto expected = new UserDto(1L, "name", "email");
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.PATCH), any(), eq(UserDto.class)))
                .thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));
        UserDto result = userClient.patchUser(req, 1L);
        assertEquals(expected, result);
    }

    @Test
    void deleteUser_success() {
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.DELETE), any(), eq(Void.class))).thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        assertDoesNotThrow(() -> userClient.deleteUser(1L));
    }

    @Test
    void createUser_shouldThrowOnError() {
        PostUserRequest req = new PostUserRequest();
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.POST), any(), eq(UserDto.class)))
                .thenThrow(mock(HttpStatusCodeException.class));
        assertThrows(HttpStatusCodeException.class, () -> userClient.createUser(req));
    }

    @Test
    void getUser_non2xxWithBody_returnsBody() {
        UserDto expected = new UserDto(1L, "name", "email");
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), eq(UserDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(expected));
        UserDto result = new UserClient("http://localhost", builder)
                .getUser(1L);
        assertEquals(expected, result);
    }

    @Test
    void getUser_non2xxWithoutBody_returnsNoBody() {
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), eq(UserDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        UserDto result = new UserClient("http://localhost", builder)
                .getUser(1L);
        assertNull(result);
    }
}
