package ru.practicum.shareit.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnedItemDto;
import ru.practicum.shareit.item.dto.PatchItemRequest;
import ru.practicum.shareit.item.dto.PostCommentRequest;
import ru.practicum.shareit.item.dto.PostItemRequest;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(serverUrl, builder, API_PREFIX);
    }

    public ItemDto getItem(long itemId) {
        ResponseEntity<ItemDto> response = get("/" + itemId, ItemDto.class);
        return response.getBody();
    }

    public List<OwnedItemDto> getOwnerItems(long ownerId) {
        ResponseEntity<List<OwnedItemDto>> response = getList("", ownerId, OwnedItemDto.class);
        return response.getBody();
    }

    public List<ItemDto> searchItems(String text) {
        ResponseEntity<List<ItemDto>> response = getList("/search?text=" + text, ItemDto.class);
        return response.getBody();
    }

    public ItemDto createItem(PostItemRequest request, long ownerId) {
        ResponseEntity<ItemDto> response = post("", ownerId, request, ItemDto.class);
        return response.getBody();
    }

    public ItemDto patchItem(long itemId, PatchItemRequest request, long ownerId) {
        ResponseEntity<ItemDto> response = patch("/" + itemId, ownerId, request, ItemDto.class);
        return response.getBody();
    }

    public CommentDto addComment(PostCommentRequest request, long authorId, long itemId) {
        ResponseEntity<CommentDto> response = post("/" + itemId + "/comment", authorId, request, CommentDto.class);
        return response.getBody();
    }

    public List<CommentDto> getAllCommentsForItem(long itemId) {
        ResponseEntity<List<CommentDto>> response = getList("/" + itemId + "/comment", CommentDto.class);
        return response.getBody();
    }

    public List<CommentDto> getAllCommentsForOwner(long ownerId) {
        ResponseEntity<List<CommentDto>> response = getList("/comments", ownerId, CommentDto.class);
        return response.getBody();
    }
}
