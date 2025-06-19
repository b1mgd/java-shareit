package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ShortItem;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
public class RequestDto {
    private long id;
    private String description;
    private long requestorId;
    private LocalDateTime created;
    private Set<ShortItem> items;
}
