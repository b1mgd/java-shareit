package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpStatusCodeException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostBookingRequest;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.dto.Status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingControllerImpl.class)
public class BookingControllerImplTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mockMvc;

    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime start = now.plusDays(1);
    private final LocalDateTime end = now.plusDays(2);

    private final BookingDto bookingDto = new BookingDto(
            1L,
            start,
            end,
            null,
            null,
            Status.APPROVED
    );

    private final PostBookingRequest validRequest = new PostBookingRequest(
            10L,
            start,
            end
    );

    @Test
    void addBooking_whenValidRequest_shouldReturnCreated() throws Exception {
        when(bookingClient.addBooking(anyLong(), any(PostBookingRequest.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(validRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void addBooking_whenUserNotFound_shouldReturnNotFound() throws Exception {
        String errorMessage = "User with id=999 was not found";
        when(bookingClient.addBooking(anyLong(), any(PostBookingRequest.class)))
                .thenThrow(new HttpStatusCodeException(HttpStatus.NOT_FOUND, errorMessage) {
                    @Override
                    public String getMessage() {
                        return errorMessage;
                    }
                });

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "999")
                        .content(objectMapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(errorMessage));
    }

    @Test
    void addBooking_withoutUserIdHeader_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBooking_withInvalidDates_shouldReturnBadRequest() throws Exception {
        PostBookingRequest invalidRequest = new PostBookingRequest(
                10L,
                end,
                start
        );

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void considerBooking_whenValidRequest_shouldReturnOk() throws Exception {
        when(bookingClient.considerBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void considerBooking_whenBookingNotFound_shouldReturnNotFound() throws Exception {
        String errorMessage = "Booking with id=999 was not found";
        when(bookingClient.considerBooking(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new HttpStatusCodeException(HttpStatus.NOT_FOUND, errorMessage) {
                    @Override
                    public String getMessage() {
                        return errorMessage;
                    }
                });

        mockMvc.perform(patch("/bookings/{bookingId}", 999L)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(errorMessage));
    }

    @Test
    void getBooking_whenValidRequest_shouldReturnOk() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getUserBookings_whenValidRequest_shouldReturnOk() throws Exception {
        when(bookingClient.getUserBookings(anyLong(), any(State.class)))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    void getUserBookings_whenInvalidState_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "INVALID_STATE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown state: INVALID_STATE"));
    }

    @Test
    void getUserBookings_withoutState_shouldReturnOk() throws Exception {
        when(bookingClient.getUserBookings(anyLong(), any(State.class)))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getOwnedItemsBookings_whenValidRequest_shouldReturnOk() throws Exception {
        when(bookingClient.getOwnedItemsBookings(anyLong(), any(State.class)))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    void getOwnedItemsBookings_withoutUserIdHeader_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBooking_whenInternalError_shouldReturnServerError() throws Exception {
        when(bookingClient.addBooking(anyLong(), any(PostBookingRequest.class)))
                .thenThrow(new HttpStatusCodeException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error") {
                });

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(validRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void addBooking_withEmptyBody_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBooking_withNullDates_shouldReturnCreated() throws Exception {
        PostBookingRequest request = new PostBookingRequest(10L, null, null);
        when(bookingClient.addBooking(anyLong(), any(PostBookingRequest.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void considerBooking_withoutApprovedParam_shouldUseDefaultTrue() throws Exception {
        when(bookingClient.considerBooking(anyLong(), anyLong(), eq(true)))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
