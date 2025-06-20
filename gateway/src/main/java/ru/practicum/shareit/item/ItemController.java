package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnedItemDto;
import ru.practicum.shareit.item.dto.PatchItemRequest;
import ru.practicum.shareit.item.dto.PostCommentRequest;
import ru.practicum.shareit.item.dto.PostItemRequest;

import java.util.List;

public interface ItemController {

    ItemDto getItem(@Positive long itemId);

    List<OwnedItemDto> getOwnerItems(@Positive long ownerId);

    List<ItemDto> searchItems(@NotBlank String text);

    ItemDto createItem(@Valid PostItemRequest request, @Positive long ownerId);

    ItemDto patchItem(@Positive long itemId, @Valid PatchItemRequest request, @Positive long ownerId);

    CommentDto addComment(@Valid PostCommentRequest request, @Positive long authorId, @Positive long itemId);

    List<CommentDto> getAllCommentsForItem(@Positive long itemId);

    List<CommentDto> getAllCommentsForOwner(@Positive long ownerId);
}
