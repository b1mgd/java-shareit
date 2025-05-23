package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemRequest;
import ru.practicum.shareit.item.dto.PostItemRequest;

import java.util.List;

public interface ItemService {

    ItemDto getItem(long itemId);

    List<ItemDto> getOwnerItems(long ownerId);

    List<ItemDto> searchItems(String text);

    ItemDto createItem(PostItemRequest request, long ownerId);

    ItemDto patchItem(long itemId, PatchItemRequest request, long ownerId);
}
