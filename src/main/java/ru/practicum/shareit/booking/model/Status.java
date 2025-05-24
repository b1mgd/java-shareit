package ru.practicum.shareit.booking.model;

public enum Status {
    WAITING, // new booking
    APPROVED, // approved booking
    REJECTED, // declined by owner
    CANCELED // declined by creator of booking
}
