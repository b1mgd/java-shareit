package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.start <= :currentTime " +
            "AND b.end >= :currentTime " +
            "ORDER BY b.start DESC ")
    List<Booking> findCurrentByBookerId(@Param("userId") long userId,
                                        @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start <= :currentTime " +
            "AND b.end >= :currentTime " +
            "ORDER BY b.start DESC ")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") long ownerId,
                                       @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.end < :currentTime " +
            "ORDER BY b.start DESC ")
    List<Booking> findPastByBookerId(@Param("userId") long userId,
                                     @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.end < :currentTime " +
            "ORDER BY b.start DESC ")
    List<Booking> findPastByOwnerId(@Param("ownerId") long ownerId,
                                    @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.start > :currentTime " +
            "ORDER BY b.start DESC ")
    List<Booking> findFutureByBookerId(@Param("userId") long userId,
                                       @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :currentTime " +
            "ORDER BY b.start DESC ")
    List<Booking> findFutureByOwnerId(@Param("ownerId") long ownerId,
                                      @Param("currentTime") LocalDateTime currentTime);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status);

    List<Booking> findAllByItemIdIn(List<Long> itemIds);

    Optional<Booking> findFirstByItemIdAndBookerIdAndStatusAndEndBefore(long itemId, long authorId,
                                                                        Status status, LocalDateTime currentTime);
}