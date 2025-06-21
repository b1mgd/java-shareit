package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.booking.dto.PostBookingRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingControllerImpl.class)
class BookingControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getBooking_shouldReturnBooking() throws Exception {
        BookingDto booking = new BookingDto();
        booking.setId(1L);
        Mockito.when(bookingService.getBooking(1L, 1L)).thenReturn(booking);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void addBooking_shouldReturnCreatedBooking() throws Exception {
        PostBookingRequest request = new PostBookingRequest();
        request.setItemId(2L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto booking = new BookingDto();
        booking.setId(2L);
        Mockito.when(bookingService.addBooking(Mockito.eq(1L), Mockito.any())).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    void considerBooking_shouldReturnUpdatedBooking() throws Exception {
        BookingDto booking = new BookingDto();
        booking.setId(3L);
        Mockito.when(bookingService.considerBooking(1L, 3L, true)).thenReturn(booking);

        mockMvc.perform(patch("/bookings/3")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L));
    }

    @Test
    void getUserBookings_shouldReturnList() throws Exception {
        BookingDto booking = new BookingDto();
        booking.setId(4L);
        Mockito.when(bookingService.getUserBookings(1L, State.ALL)).thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4L));
    }

    @Test
    void getOwnedItemsBookings_shouldReturnList() throws Exception {
        BookingDto booking = new BookingDto();
        booking.setId(5L);
        Mockito.when(bookingService.getOwnedItemsBookings(1L, State.ALL)).thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5L));
    }
}
