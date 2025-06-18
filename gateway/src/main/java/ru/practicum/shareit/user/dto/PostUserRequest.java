package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostUserRequest {

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 1, max = 255, message = "Имя пользователя должно содержать от 1 до 255 символов")
    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    @Size(max = 255, message = "Email не может превышать 255 символов")
    private String email;
}
