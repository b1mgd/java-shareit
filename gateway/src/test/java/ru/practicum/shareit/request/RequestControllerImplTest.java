package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpStatusCodeException;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RequestControllerImpl.class)
public class RequestControllerImplTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestClient requestClient;

    @Autowired
    private MockMvc mockMvc;

    private final RequestDto requestDto = new RequestDto(1L, "desc", 1L, LocalDateTime.now(), Set.of());
    private final PostRequestDto validRequest = new PostRequestDto();

    @Test
    void createRequest_whenValid_shouldReturnCreated() throws Exception {
        validRequest.setDescription("desc");
        when(requestClient.createRequest(any(PostRequestDto.class), anyLong())).thenReturn(requestDto);
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(validRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createRequest_withoutHeader_shouldReturnBadRequest() throws Exception {
        validRequest.setDescription("desc");
        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRequest_withEmptyBody_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRequest_whenInternalError_shouldReturnServerError() throws Exception {
        validRequest.setDescription("desc");
        when(requestClient.createRequest(any(PostRequestDto.class), anyLong()))
                .thenThrow(new HttpStatusCodeException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error") {});
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void findAllRequestsByRequestorId_whenValid_shouldReturnOk() throws Exception {
        when(requestClient.findAllRequestsByRequestorId(anyLong())).thenReturn(List.of(requestDto));
        mockMvc.perform(get("/requests").header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void findAllRequestsByRequestorId_withoutHeader_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllRequests_shouldReturnOk() throws Exception {
        when(requestClient.findAllRequests()).thenReturn(List.of(requestDto));
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void findRequestById_whenValid_shouldReturnOk() throws Exception {
        when(requestClient.findRequestById(anyLong())).thenReturn(requestDto);
        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findRequestById_whenNotFound_shouldReturnNotFound() throws Exception {
        when(requestClient.findRequestById(anyLong()))
                .thenThrow(new HttpStatusCodeException(HttpStatus.NOT_FOUND, "Not found") {});
        mockMvc.perform(get("/requests/999"))
                .andExpect(status().isNotFound());
    }
}
