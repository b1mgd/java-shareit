package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ShortItem {
    private long id;
    private String name;
    private long ownerId;
}
