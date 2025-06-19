package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
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
class RequestServiceImplTest {

    @MockBean
    private RequestRepository requestRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ru.practicum.shareit.request.RequestMapper requestMapper;

    private RequestServiceImpl requestService;

    @BeforeEach
    void setUp() {
        requestService = new RequestServiceImpl(requestRepository, requestMapper, userRepository);

        when(requestRepository.findAll())
                .thenReturn(Collections.emptyList());
        when(requestRepository.findAllByRequestorId(anyLong()))
                .thenReturn(Collections.emptyList());
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
    }

    @Test
    void createRequest_shouldThrowNotFoundException_whenUserNotFound() {
        long userId = 1L;
        PostRequestDto request = mock(PostRequestDto.class);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.createRequest(request, userId));
    }

    @Test
    void findAllRequestsByRequestorId_shouldThrowValidationException_whenUserNotExists() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(ValidationException.class, () -> requestService.findAllRequestsByRequestorId(userId));
    }

    @Test
    void findAllRequests_shouldReturnEmptyList_whenNoRequests() {
        when(requestRepository.findAll()).thenReturn(Collections.emptyList());
        List<RequestDto> result = requestService.findAllRequests();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findRequestById_shouldThrowNotFoundException_whenRequestNotFound() {
        long requestId = 1L;
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.findRequestById(requestId));
    }

    @Test
    void createRequest_shouldReturnRequestDto_whenValid() {
        long userId = 1L;
        User user = new User();
        PostRequestDto request = mock(PostRequestDto.class);
        Request req = new Request();
        Request savedReq = new Request();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.save(any())).thenReturn(savedReq);
        RequestDto result = requestService.createRequest(request, userId);
        assertNotNull(result);
    }

    @Test
    void findAllRequestsByRequestorId_shouldReturnList_whenRequestsExist() {
        long userId = 1L;
        Request req = new Request();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllByRequestorId(userId)).thenReturn(List.of(req));
        List<RequestDto> result = requestService.findAllRequestsByRequestorId(userId);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllRequests_shouldReturnList_whenRequestsExist() {
        Request req = new Request();
        when(requestRepository.findAll()).thenReturn(List.of(req));
        List<RequestDto> result = requestService.findAllRequests();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findRequestById_shouldReturnRequestDto_whenRequestExists() {
        long requestId = 1L;
        Request req = new Request();
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(req));
        RequestDto result = requestService.findRequestById(requestId);
        assertNotNull(result);
    }

    @Test
    void findAllRequestsByRequestorId_shouldReturnEmptyList_whenNoRequests() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllByRequestorId(userId)).thenReturn(Collections.emptyList());
        List<RequestDto> result = requestService.findAllRequestsByRequestorId(userId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
