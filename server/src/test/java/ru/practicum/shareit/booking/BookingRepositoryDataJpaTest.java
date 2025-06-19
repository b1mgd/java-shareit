package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryDataJpaTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager em;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User(null, "owner", "owner@email.com");
        booker = new User(null, "booker", "booker@email.com");
        em.persist(owner);
        em.persist(booker);
        item = new Item(null, "item", "desc", true, owner, null);
        em.persist(item);
        booking = new Booking(null,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(2),
                item, booker, Status.APPROVED);
        em.persist(booking);
        em.flush();
    }

    @Test
    @DisplayName("findAllByBookerIdOrderByStartDesc возвращает бронирования пользователя")
    void findAllByBookerIdOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId());
        assertThat(bookings).isNotEmpty().contains(booking);
    }

    @Test
    @DisplayName("findAllByItemOwnerIdOrderByStartDesc возвращает бронирования владельца")
    void findAllByItemOwnerIdOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId());
        assertThat(bookings).isNotEmpty().contains(booking);
    }

    @Test
    @DisplayName("findAllByBookerIdAndStatusOrderByStartDesc возвращает по статусу")
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), Status.APPROVED);
        assertThat(bookings).isNotEmpty().contains(booking);
    }

    @Test
    @DisplayName("findCurrentByBookerId возвращает текущие бронирования")
    void findCurrentByBookerId() {
        List<Booking> bookings = bookingRepository.findCurrentByBookerId(booker.getId(), LocalDateTime.now());
        assertThat(bookings).isNotEmpty().contains(booking);
    }

    @Test
    @DisplayName("findPastByBookerId не возвращает будущие бронирования")
    void findPastByBookerId() {
        List<Booking> bookings = bookingRepository.findPastByBookerId(booker.getId(), LocalDateTime.now().minusDays(3));
        assertThat(bookings).isEmpty();
    }

    @Test
    @DisplayName("findFutureByBookerId возвращает будущие бронирования")
    void findFutureByBookerId() {
        Booking future = new Booking(null,
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4),
                item, booker, Status.WAITING);
        em.persist(future);
        em.flush();
        List<Booking> bookings = bookingRepository.findFutureByBookerId(booker.getId(), LocalDateTime.now());
        assertThat(bookings).contains(future);
    }

    @Test
    @DisplayName("findFirstByItemIdAndBookerIdAndStatusAndEndBefore находит бронирование")
    void findFirstByItemIdAndBookerIdAndStatusAndEndBefore() {
        Booking past = new Booking(null,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(4),
                item, booker, Status.APPROVED);
        em.persist(past);
        em.flush();
        Optional<Booking> found = bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                item.getId(), booker.getId(), Status.APPROVED, LocalDateTime.now());
        assertThat(found).isPresent();
    }
}