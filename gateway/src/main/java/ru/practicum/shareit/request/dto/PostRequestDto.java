package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostRequestDto {

    @NotBlank(message = "Описание запроса не может быть пустым")
    @Size(max = 1000, message = "Описание запроса должно содержать от 1 до 1000 символов")
    private String description;
}
