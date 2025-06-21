package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class PatchUserRequest {
    private String name;
    private String email;
}
