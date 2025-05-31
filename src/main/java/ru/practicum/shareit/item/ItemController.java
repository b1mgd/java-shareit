package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemController {

    ItemDto getItem(@Positive long itemId);

    List<OwnedItemDto> getOwnerItems(@Positive long ownerId);

    List<ItemDto> searchItems(@NotNull String text);

    ItemDto createItem(@Valid PostItemRequest request, @Positive long ownerId);

    ItemDto patchItem(@Positive long itemId, @Valid PatchItemRequest request, @Positive long ownerId);

    CommentDto addComment(@Valid PostCommentRequest request, @Positive long authorId, long itemId);

    List<CommentDto> getAllCommentsForItem(@Positive long itemId);

    List<CommentDto> getAllCommentsForOwner(@Positive long ownerId);
}
