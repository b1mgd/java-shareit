package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostBookingRequest {

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    private Long itemId;
}
