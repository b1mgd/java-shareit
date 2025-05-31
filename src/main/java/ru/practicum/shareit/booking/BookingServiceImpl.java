package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.AccessForbiddenException;
import ru.practicum.shareit.exception.ArgumentsNotValidException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemJpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserJpaRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingJpaRepository bookingRepository;
    private final ItemJpaRepository itemRepository;
    private final UserJpaRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto addBooking(long userId, PostBookingRequest request) {
        long itemId = request.getItemId();
        Item item = getItem(itemId);
        User owner = item.getOwner();
        User booker = getUser(userId);

        if (owner.equals(booker)) {
            throw new ValidationException("Не допускается бронирование вещи ее хозяином с userId " + userId);
        }

        if (!item.isAvailable()) {
            throw new ArgumentsNotValidException("В настоящий момент вещь с itemId " + itemId + " не доступна");
        }

        LocalDateTime start = request.getStart();
        LocalDateTime end = request.getEnd();

        if (start.isAfter(end)
                || start.isEqual(end)
                || start.isBefore(LocalDateTime.now())
                || end.isBefore(LocalDateTime.now())) {
            throw new ArgumentsNotValidException("Некорректные даты бронирования. Start: " + start + " End: " + end);
        }

        Booking booking = bookingMapper.mapToBooking(request);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        BookingDto result = bookingMapper.mapToBookingDto(savedBooking);
        log.info("Бронирование сохранено: {}", result);

        return result;
    }

    @Override
    public BookingDto considerBooking(Long userId, long bookingId, boolean approved) {

        Booking booking = getBooking(bookingId);

        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new AccessForbiddenException("Изменение статуса бронирования bookingId " + bookingId +
                    " возможно только его владельцем");
        }

        validateUser(userId);

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        Booking savedBooking = bookingRepository.save(booking);
        BookingDto result = bookingMapper.mapToBookingDto(savedBooking);
        log.info("Статус бронирования изменен: {}", result);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBooking(long userId, long bookingId) {
        validateUser(userId);
        Booking booking = getBooking(bookingId);
        long ownerId = booking.getItem().getOwner().getId();
        long bookerId = booking.getBooker().getId();

        if (userId != ownerId && userId != bookerId) {
            throw new ValidationException("Доступ к бронированию " + bookingId +
                    " разрешен только владельцу или пользователю, оставившему бронирование");
        }

        BookingDto result = bookingMapper.mapToBookingDto(booking);
        log.info("Получен результат: {}", result);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getUserBookings(long userId, State state) {
        validateUser(userId);
        Status status = Status.APPROVED;

        List<Booking> userBookings = switch (state) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findCurrentByBookerId(userId, LocalDateTime.now());
            case PAST -> bookingRepository.findPastByBookerId(userId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findFutureByBookerId(userId, LocalDateTime.now());
            case WAITING -> {
                status = Status.WAITING;
                yield bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, status);
            }
            case REJECTED -> {
                status = Status.REJECTED;
                yield bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, status);
            }
        };

        List<BookingDto> result = userBookings
                .stream()
                .map(bookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
        log.info("Получен результат: {}", result);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getOwnedItemsBookings(long userId, State state) {
        validateUser(userId);
        List<Item> userItems = itemRepository.findAllByOwnerId(userId);

        if (userItems.isEmpty()) {
            throw new NotFoundException("Предметы, размещенные пользователем с userId " + userId + ", не найдены");
        }

        Status status = Status.APPROVED;

        List<Booking> userBookings = switch (state) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findCurrentByOwnerId(userId, LocalDateTime.now());
            case PAST -> bookingRepository.findPastByOwnerId(userId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findFutureByOwnerId(userId, LocalDateTime.now());
            case WAITING -> {
                status = Status.WAITING;
                yield bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, status);
            }
            case REJECTED -> {
                status = Status.REJECTED;
                yield bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, status);
            }
        };

        List<BookingDto> result = userBookings
                .stream()
                .map(bookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
        log.info("Получен результат: {}", result);

        return result;
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с userId " + userId + " не был найден"));
    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с itemId " + itemId + " не был найден"));
    }

    private Booking getBooking(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с bookingId " + bookingId +
                        " не было обнаружено"));
    }

    private void validateUser(long userId) {
        boolean exists = userRepository.existsById(userId);

        if (!exists) {
            throw new NotFoundException("Пользователь с userId " + userId + " не был найден");
        }
    }
}
