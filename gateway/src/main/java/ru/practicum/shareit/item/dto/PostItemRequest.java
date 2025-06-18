package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostItemRequest {

    @NotBlank(message = "Название предмета не может быть пустым")
    @Size(max = 255, message = "Название предмета должно содержать от 1 до 255 символов")
    private String name;

    @NotBlank(message = "Описание предмета не может быть пустым")
    @Size(max = 1000, message = "Описание предмета должно содержать от 1 до 1000 символов")
    private String description;

    @NotNull(message = "Статус доступности предмета должен быть указан")
    private Boolean available;

    private Long requestId;
}
