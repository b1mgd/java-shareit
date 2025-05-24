package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemRequest;
import ru.practicum.shareit.item.dto.PostItemRequest;

import java.util.List;

public interface ItemController {

    ItemDto getItem(@Positive long itemId);

    List<ItemDto> getOwnerItems(@Positive long ownerId);

    List<ItemDto> searchItems(@NotNull String text);

    ItemDto createItem(@Valid PostItemRequest request, @Positive long ownerId);

    ItemDto patchItem(@Positive long itemId, @Valid PatchItemRequest request, @Positive long ownerId);
}
