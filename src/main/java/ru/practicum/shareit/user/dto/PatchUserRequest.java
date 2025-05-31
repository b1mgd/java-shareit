package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class PatchUserRequest {

    private String name;

    @Email
    private String email;
}
