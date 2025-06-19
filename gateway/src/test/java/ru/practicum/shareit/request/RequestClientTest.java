package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RequestClientTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private RestTemplateBuilder builder;
    private RequestClient requestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(builder.uriTemplateHandler(any())).thenReturn(builder);
        when(builder.requestFactory(any(java.util.function.Supplier.class))).thenReturn(builder);
        when(builder.build()).thenReturn(restTemplate);
        requestClient = new RequestClient("http://localhost", builder);
    }

    @Test
    void createRequest_success() {
        PostRequestDto req = new PostRequestDto();
        RequestDto expected = new RequestDto(1L, "desc", 1L, null, null);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.POST), any(), eq(RequestDto.class)))
                .thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));
        RequestDto result = requestClient.createRequest(req, 1L);
        assertEquals(expected, result);
    }

    @Test
    void findAllRequestsByRequestorId_success() {
        RequestDto dto = new RequestDto(1L, "desc", 1L, null, null);
        List<RequestDto> expected = List.of(dto);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), any(org.springframework.core.ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity(expected, HttpStatus.OK));
        List<RequestDto> result = requestClient.findAllRequestsByRequestorId(1L);
        assertEquals(expected, result);
    }

    @Test
    void findAllRequests_success() {
        RequestDto dto = new RequestDto(1L, "desc", 1L, null, null);
        List<RequestDto> expected = List.of(dto);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), any(org.springframework.core.ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity(expected, HttpStatus.OK));
        List<RequestDto> result = requestClient.findAllRequests();
        assertEquals(expected, result);
    }

    @Test
    void findRequestById_success() {
        RequestDto expected = new RequestDto(1L, "desc", 1L, null, null);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), eq(RequestDto.class)))
                .thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));
        RequestDto result = requestClient.findRequestById(1L);
        assertEquals(expected, result);
    }

    @Test
    void createRequest_shouldThrowOnError() {
        PostRequestDto req = new PostRequestDto();
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.POST), any(), eq(RequestDto.class)))
                .thenThrow(mock(HttpStatusCodeException.class));
        assertThrows(HttpStatusCodeException.class, () -> requestClient.createRequest(req, 1L));
    }
}