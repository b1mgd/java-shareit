package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostCommentRequest {

    @NotBlank
    private String text;
}
