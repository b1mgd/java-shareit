package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostBookingRequest;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingControllerImpl implements BookingController {
    private final BookingService bookingService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody PostBookingRequest request) {
        log.info("Запрос на бронирование от пользователя с userId {}: {}", userId, request);
        return bookingService.addBooking(userId, request);
    }

    @Override
    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto considerBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long bookingId,
                                      @RequestParam (defaultValue = "true") boolean approved) {
        log.info("Заявка на бронирование с BookingId: {} рассматривается пользователем с userId {}. Approved: {}",
                userId, bookingId, approved);
        return bookingService.considerBooking(userId, bookingId, approved);

    }

    @Override
    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long bookingId) {
        log.info("Запрос на получение информации о бронировании с bookingId: {} пользователем с userId: {}.",
                bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(defaultValue = "ALL") State state) {
        log.info("Запрос на получение всех бронирований пользователя userId: {} со статусом {}.", userId, state);
        return bookingService.getUserBookings(userId, state);
    }

    @Override
    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getOwnedItemsBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "ALL") State state) {
        log.info("Запрос на получение списка бронирований со статусом {} по предметам пользователя с userId {}.",
                state, userId);
        return bookingService.getOwnedItemsBookings(userId, state);
    }
}
