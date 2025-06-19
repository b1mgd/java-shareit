package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.AccessForbiddenException;
import ru.practicum.shareit.exception.ArgumentsNotValidException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.AfterEach;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    // @Mock
    // private BookingMapper bookingMapper;
    private BookingMapper bookingMapper = new BookingMapper() {
        @Override
        public Booking mapToBooking(PostBookingRequest request) {
            Booking booking = new Booking();
            booking.setId(100L);
            return booking;
        }

        @Override
        public BookingDto mapToBookingDto(Booking booking) {
            BookingDto dto = new BookingDto();
            dto.setId(booking != null ? booking.getId() : null);
            return dto;
        }
    };

    private BookingServiceImpl bookingService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository, bookingMapper);
    }

    @Test
    void addBooking_shouldThrowValidationException_whenOwnerBooksOwnItem() {
        long userId = 1L;
        long itemId = 2L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(user);
        item.setAvailable(true);
        PostBookingRequest request = mock(PostBookingRequest.class);
        when(request.getItemId()).thenReturn(itemId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ValidationException ex = assertThrows(ValidationException.class, () ->
                bookingService.addBooking(userId, request));
        assertTrue(ex.getMessage().contains("хозяином"));
    }

    @Test
    void getBooking_shouldThrowNotFoundException_whenBookingNotFound() {
        long userId = 1L;
        long bookingId = 10L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                bookingService.getBooking(userId, bookingId));
        assertTrue(ex.getMessage().contains("не было обнаружено"));
    }

    @Test
    void getUserBookings_shouldReturnEmptyList_whenNoBookings() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(userId)).thenReturn(Collections.emptyList());

        List<BookingDto> result = bookingService.getUserBookings(userId, State.ALL);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addBooking_shouldCreateBooking_whenValidRequest() {
        long userId = 1L;
        long itemId = 2L;
        User user = new User();
        user.setId(userId);
        User owner = new User();
        owner.setId(3L);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);
        item.setAvailable(true);
        PostBookingRequest request = mock(PostBookingRequest.class);
        when(request.getItemId()).thenReturn(itemId);
        when(request.getStart()).thenReturn(LocalDateTime.now().plusDays(1));
        when(request.getEnd()).thenReturn(LocalDateTime.now().plusDays(2));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Booking booking = new Booking();
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.addBooking(userId, request);
        assertNotNull(result);
        verify(bookingRepository).save(any());
    }

    @Test
    void addBooking_shouldThrowArgumentsNotValidException_whenItemNotAvailable() {
        long userId = 1L;
        long itemId = 2L;
        User user = new User();
        user.setId(userId);
        User owner = new User();
        owner.setId(3L);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);
        item.setAvailable(false);
        PostBookingRequest request = mock(PostBookingRequest.class);
        when(request.getItemId()).thenReturn(itemId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ArgumentsNotValidException ex = assertThrows(ArgumentsNotValidException.class, () ->
                bookingService.addBooking(userId, request));
        assertTrue(ex.getMessage().contains("не доступна"));
    }

    @Test
    void addBooking_shouldThrowArgumentsNotValidException_whenDatesInvalid() {
        long userId = 1L;
        long itemId = 2L;
        User user = new User();
        user.setId(userId);
        User owner = new User();
        owner.setId(3L);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);
        item.setAvailable(true);
        PostBookingRequest request = mock(PostBookingRequest.class);
        when(request.getItemId()).thenReturn(itemId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(request.getStart()).thenReturn(LocalDateTime.now().plusDays(2));
        when(request.getEnd()).thenReturn(LocalDateTime.now().plusDays(1));

        ArgumentsNotValidException ex = assertThrows(ArgumentsNotValidException.class, () ->
                bookingService.addBooking(userId, request));
        assertTrue(ex.getMessage().contains("Некорректные даты"));
    }

    @Test
    void considerBooking_shouldApproveBooking_whenOwnerApproves() {
        long ownerId = 1L;
        long bookingId = 10L;
        User owner = new User();
        owner.setId(ownerId);
        Item item = new Item();
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.considerBooking(ownerId, bookingId, true);
        assertNotNull(result);
        verify(bookingRepository).save(any());
    }

    @Test
    void considerBooking_shouldRejectBooking_whenOwnerRejects() {
        long ownerId = 1L;
        long bookingId = 10L;
        User owner = new User();
        owner.setId(ownerId);
        Item item = new Item();
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.considerBooking(ownerId, bookingId, false);
        assertNotNull(result);
        verify(bookingRepository).save(any());
    }

    @Test
    void considerBooking_shouldThrowAccessForbiddenException_whenNotOwner() {
        long userId = 2L;
        long bookingId = 10L;
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        AccessForbiddenException ex = assertThrows(AccessForbiddenException.class, () ->
                bookingService.considerBooking(userId, bookingId, true));
        assertTrue(ex.getMessage().contains("только его владельцем"));
    }

    @Test
    void getBooking_shouldReturnBooking_whenOwnerOrBooker() {
        long userId = 1L;
        long bookingId = 10L;
        User owner = new User();
        owner.setId(userId);
        User booker = new User();
        booker.setId(2L);
        Item item = new Item();
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setBooker(booker);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBooking(userId, bookingId);
        assertNotNull(result);
        assertEquals(bookingId, result.getId());
    }

    @Test
    void getBooking_shouldThrowValidationException_whenNotOwnerOrBooker() {
        long userId = 3L;
        long bookingId = 10L;
        User owner = new User();
        owner.setId(1L);
        User booker = new User();
        booker.setId(2L);
        Item item = new Item();
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setBooker(booker);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        ValidationException ex = assertThrows(ValidationException.class, () ->
                bookingService.getBooking(userId, bookingId));
        assertTrue(ex.getMessage().contains("Доступ к бронированию"));
    }

    @Test
    void getUserBookings_shouldReturnBookingsForAllStates() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        for (State state : State.values()) {
            List<Booking> bookings = List.of(new Booking());
            switch (state) {
                case ALL -> when(bookingRepository.findAllByBookerIdOrderByStartDesc(userId)).thenReturn(bookings);
                case CURRENT ->
                        when(bookingRepository.findCurrentByBookerId(userId, LocalDateTime.now())).thenReturn(bookings);
                case PAST ->
                        when(bookingRepository.findPastByBookerId(userId, LocalDateTime.now())).thenReturn(bookings);
                case FUTURE ->
                        when(bookingRepository.findFutureByBookerId(userId, LocalDateTime.now())).thenReturn(bookings);
                case WAITING ->
                        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING)).thenReturn(bookings);
                case REJECTED ->
                        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED)).thenReturn(bookings);
            }
            List<BookingDto> result = bookingService.getUserBookings(userId, state);
            assertNotNull(result);
        }
    }

    @Test
    void getOwnedItemsBookings_shouldThrowNotFoundException_whenNoItems() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findAllByOwnerId(userId)).thenReturn(Collections.emptyList());

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                bookingService.getOwnedItemsBookings(userId, State.ALL));
        assertTrue(ex.getMessage().contains("не найдены"));
    }

    @Test
    void getOwnedItemsBookings_shouldReturnBookingsForAllStates() {
        long userId = 1L;
        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User());
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findAllByOwnerId(userId)).thenReturn(List.of(item));
        for (State state : State.values()) {
            List<Booking> bookings = List.of(new Booking());
            switch (state) {
                case ALL -> when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId)).thenReturn(bookings);
                case CURRENT ->
                        when(bookingRepository.findCurrentByOwnerId(userId, LocalDateTime.now())).thenReturn(bookings);
                case PAST ->
                        when(bookingRepository.findPastByOwnerId(userId, LocalDateTime.now())).thenReturn(bookings);
                case FUTURE ->
                        when(bookingRepository.findFutureByOwnerId(userId, LocalDateTime.now())).thenReturn(bookings);
                case WAITING ->
                        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING)).thenReturn(bookings);
                case REJECTED ->
                        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED)).thenReturn(bookings);
            }
            List<BookingDto> result = bookingService.getOwnedItemsBookings(userId, state);
            assertNotNull(result);
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}