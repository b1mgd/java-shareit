package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto getItem(long itemId);

    List<OwnedItemDto> getOwnerItems(long ownerId);

    List<ItemDto> searchItems(String text);

    ItemDto createItem(PostItemRequest request, long ownerId);

    ItemDto patchItem(long itemId, PatchItemRequest request, long ownerId);

    CommentDto addComment(PostCommentRequest request, long itemId, Long authorId);

    List<CommentDto> getAllCommentsForItem(long itemId);

    List<CommentDto> getAllCommentsForOwner(long ownerId);
}
