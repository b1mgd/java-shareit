package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PatchItemRequest {
    @Size(min = 1, max = 255, message = "Название предмета должно содержать от 1 до 255 символов")
    private String name;
    
    @Size(min = 1, max = 1000, message = "Описание предмета должно содержать от 1 до 1000 символов")
    private String description;
    
    private Boolean available;
}
