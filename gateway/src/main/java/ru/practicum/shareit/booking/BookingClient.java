package ru.practicum.shareit.booking;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostBookingRequest;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(serverUrl, builder, API_PREFIX);
    }

    public BookingDto addBooking(long userId, PostBookingRequest request) {
        ResponseEntity<BookingDto> response = post("", userId, request, BookingDto.class);
        return response.getBody();
    }

    public BookingDto considerBooking(long userId, long bookingId, boolean approved) {
        Map<String, Object> parameters = Map.of("approved", approved);
        ResponseEntity<BookingDto> response = patch("/" + bookingId, userId, parameters, null, BookingDto.class);
        return response.getBody();
    }

    public BookingDto getBooking(long userId, long bookingId) {
        ResponseEntity<BookingDto> response = get("/" + bookingId, userId, BookingDto.class);
        return response.getBody();
    }

    public List<BookingDto> getUserBookings(long userId, State state) {
        Map<String, Object> parameters = Map.of("state", state.name());
        ResponseEntity<List<BookingDto>> response = getList("", userId, parameters, BookingDto.class);
        return response.getBody();
    }

    public List<BookingDto> getOwnedItemsBookings(long userId, State state) {
        Map<String, Object> parameters = Map.of("state", state.name());
        ResponseEntity<List<BookingDto>> response = getList("/owner", userId, parameters, BookingDto.class);
        return response.getBody();
    }
}
