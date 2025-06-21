package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OwnedItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;
    private LocalDateTime lastStart;
    private LocalDateTime lastEnd;
    private LocalDateTime nextStart;
    private LocalDateTime nextEnd;
}
