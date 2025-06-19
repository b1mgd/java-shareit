package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostBookingRequest;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.dto.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingClientTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private RestTemplateBuilder builder;
    private BookingClient bookingClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(builder.uriTemplateHandler(any())).thenReturn(builder);
        when(builder.requestFactory(any(java.util.function.Supplier.class))).thenReturn(builder);
        when(builder.build()).thenReturn(restTemplate);
        bookingClient = new BookingClient("http://localhost", builder);
    }

    @Test
    void addBooking_success() {
        PostBookingRequest request = new PostBookingRequest();
        BookingDto expected = new BookingDto(1L, null, null, null, null, Status.APPROVED);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.POST), any(), eq(BookingDto.class)))
                .thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));
        BookingDto result = bookingClient.addBooking(1L, request);
        assertEquals(expected, result);
    }

    @Test
    void considerBooking_success() {
        BookingDto expected = new BookingDto(1L, null, null, null, null, Status.APPROVED);
        when(restTemplate.exchange(contains("/1"), eq(org.springframework.http.HttpMethod.PATCH), any(), eq(BookingDto.class), anyMap()))
                .thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));
        BookingDto result = bookingClient.considerBooking(1L, 1L, true);
        assertEquals(expected, result);
    }

    @Test
    void getBooking_success() {
        BookingDto expected = new BookingDto(1L, null, null, null, null, Status.APPROVED);
        when(restTemplate.exchange(contains("/1"), eq(org.springframework.http.HttpMethod.GET), any(), eq(BookingDto.class)))
                .thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));
        BookingDto result = bookingClient.getBooking(1L, 1L);
        assertEquals(expected, result);
    }

    @Test
    void getUserBookings_success() {
        BookingDto booking = new BookingDto(1L, null, null, null, null, Status.APPROVED);
        List<BookingDto> expected = List.of(booking);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), any(org.springframework.core.ParameterizedTypeReference.class), anyMap()))
                .thenReturn(new ResponseEntity(expected, HttpStatus.OK));
        List<BookingDto> result = bookingClient.getUserBookings(1L, State.ALL);
        assertEquals(expected, result);
    }

    @Test
    void getOwnedItemsBookings_success() {
        BookingDto booking = new BookingDto(1L, null, null, null, null, Status.APPROVED);
        List<BookingDto> expected = List.of(booking);
        when(restTemplate.exchange(contains("/owner"), eq(org.springframework.http.HttpMethod.GET), any(), any(org.springframework.core.ParameterizedTypeReference.class), anyMap()))
                .thenReturn(new ResponseEntity(expected, HttpStatus.OK));
        List<BookingDto> result = bookingClient.getOwnedItemsBookings(1L, State.ALL);
        assertEquals(expected, result);
    }

    @Test
    void addBooking_shouldThrowOnError() {
        PostBookingRequest request = new PostBookingRequest();
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.POST), any(), eq(BookingDto.class)))
                .thenThrow(mock(HttpStatusCodeException.class));
        assertThrows(HttpStatusCodeException.class, () -> bookingClient.addBooking(1L, request));
    }
}