package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpStatusCodeException;
import ru.practicum.shareit.user.dto.PatchUserRequest;
import ru.practicum.shareit.user.dto.PostUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserControllerImpl.class)
public class UserControllerImplTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Autowired
    private MockMvc mockMvc;

    private final UserDto userDto = new UserDto(1L, "user", "user@email.com");
    private final PostUserRequest validPostRequest = new PostUserRequest();
    private final PatchUserRequest patchUserRequest = new PatchUserRequest();

    @Test
    void getAllUsers_shouldReturnOk() throws Exception {
        when(userClient.getAllUsers()).thenReturn(List.of(userDto));
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getUser_whenExists_shouldReturnOk() throws Exception {
        when(userClient.getUser(anyLong())).thenReturn(userDto);
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUser_whenNotFound_shouldReturnNotFound() throws Exception {
        when(userClient.getUser(anyLong()))
                .thenThrow(new HttpStatusCodeException(HttpStatus.NOT_FOUND, "Not found") {});
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_whenValid_shouldReturnCreated() throws Exception {
        validPostRequest.setName("user");
        validPostRequest.setEmail("user@email.com");
        when(userClient.createUser(any(PostUserRequest.class))).thenReturn(userDto);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(validPostRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createUser_withEmptyBody_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_whenInternalError_shouldReturnServerError() throws Exception {
        validPostRequest.setName("user");
        validPostRequest.setEmail("user@email.com");
        when(userClient.createUser(any(PostUserRequest.class)))
                .thenThrow(new HttpStatusCodeException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error") {});
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(validPostRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void patchUser_whenValid_shouldReturnOk() throws Exception {
        patchUserRequest.setName("newName");
        patchUserRequest.setEmail("new@email.com");
        when(userClient.patchUser(any(PatchUserRequest.class), anyLong())).thenReturn(userDto);
        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(patchUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void patchUser_withEmptyBody_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/users/1")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchUser_whenNotFound_shouldReturnNotFound() throws Exception {
        patchUserRequest.setName("newName");
        patchUserRequest.setEmail("new@email.com");
        when(userClient.patchUser(any(PatchUserRequest.class), anyLong()))
                .thenThrow(new HttpStatusCodeException(HttpStatus.NOT_FOUND, "Not found") {});
        mockMvc.perform(patch("/users/999")
                        .content(objectMapper.writeValueAsString(patchUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_whenValid_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_whenNotFound_shouldReturnNotFound() throws Exception {
        doThrow(new HttpStatusCodeException(HttpStatus.NOT_FOUND, "Not found") {}).when(userClient).deleteUser(anyLong());
        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound());
    }
}
