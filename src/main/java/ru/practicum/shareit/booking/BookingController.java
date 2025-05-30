package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostBookingRequest;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingController {

    BookingDto addBooking(@Positive long userId, @Valid PostBookingRequest request);

    BookingDto considerBooking(@Positive long userId, @Positive long bookingId, boolean approved);

    BookingDto getBooking(@Positive long userId, @Positive long bookingId);

    List<BookingDto> getUserBookings(@Positive long userId, State state);

    List<BookingDto> getOwnedItemsBookings(@Positive long userId, State state);
}
