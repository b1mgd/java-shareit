package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostBookingRequest;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(long userId, PostBookingRequest request);

    BookingDto considerBooking(Long bookingId, long userId, boolean approved);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> getUserBookings(long userId, State state);

    List<BookingDto> getOwnedItemsBookings(long userId, State state);
}
