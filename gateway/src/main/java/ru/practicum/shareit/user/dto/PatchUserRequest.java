package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PatchUserRequest {

    @Size(max = 255, message = "Имя пользователя должно содержать от 1 до 255 символов")
    private String name;

    @Email(message = "Некорректный формат email")
    @Size(max = 255, message = "Email не может превышать 255 символов")
    private String email;
}
