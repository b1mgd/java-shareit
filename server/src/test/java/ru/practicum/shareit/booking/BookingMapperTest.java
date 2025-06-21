package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class BookingMapperTest {
    @Autowired
    private BookingMapper bookingMapper;

    @Test
    void mapToBookingDto_shouldMapAllFields() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(Status.APPROVED);
        Item item = new Item();
        item.setId(2L);
        booking.setItem(item);
        User booker = new User();
        booker.setId(3L);
        booking.setBooker(booker);

        BookingDto dto = bookingMapper.mapToBookingDto(booking);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isNotNull();
        assertThat(dto.getEnd()).isNotNull();
        assertThat(dto.getStatus()).isEqualTo(Status.APPROVED);
        assertThat(dto.getItem()).isNotNull();
        assertThat(dto.getItem().getId()).isEqualTo(2L);
        assertThat(dto.getBooker()).isNotNull();
        assertThat(dto.getBooker().getId()).isEqualTo(3L);
    }

    @Test
    void mapToBookingDto_shouldReturnNull_whenBookingIsNull() {
        assertThat(bookingMapper.mapToBookingDto(null)).isNull();
    }

    @Test
    void mapToBooking_shouldMapAllFields() {
        PostBookingRequest req = new PostBookingRequest();
        req.setStart(LocalDateTime.now());
        req.setEnd(LocalDateTime.now().plusDays(1));
        req.setItemId(2L);

        Booking booking = bookingMapper.mapToBooking(req);
        assertThat(booking).isNotNull();
        assertThat(booking.getStart()).isNotNull();
        assertThat(booking.getEnd()).isNotNull();
    }

    @Test
    void mapToBooking_shouldReturnNull_whenRequestIsNull() {
        assertThat(bookingMapper.mapToBooking(null)).isNull();
    }
}
