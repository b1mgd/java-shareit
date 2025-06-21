package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.request.dto.PostRequestDto;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestControllerImpl.class)
class RequestControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findRequestById_shouldReturnRequest() throws Exception {
        RequestDto request = new RequestDto();
        request.setId(1L);
        Mockito.when(requestService.findRequestById(1L)).thenReturn(request);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void createRequest_shouldReturnCreatedRequest() throws Exception {
        PostRequestDto postRequest = new PostRequestDto();
        postRequest.setDescription("desc");
        RequestDto request = new RequestDto();
        request.setId(2L);
        Mockito.when(requestService.createRequest(Mockito.any(), Mockito.eq(1L))).thenReturn(request);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    void findAllRequestsByRequestorId_shouldReturnList() throws Exception {
        RequestDto request = new RequestDto();
        request.setId(3L);
        Mockito.when(requestService.findAllRequestsByRequestorId(1L)).thenReturn(List.of(request));

        mockMvc.perform(get("/requests").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L));
    }

    @Test
    void findAllRequests_shouldReturnList() throws Exception {
        RequestDto request = new RequestDto();
        request.setId(4L);
        Mockito.when(requestService.findAllRequests()).thenReturn(List.of(request));

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4L));
    }
}
