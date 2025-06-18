package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnedItemDto;
import ru.practicum.shareit.item.dto.PatchItemRequest;
import ru.practicum.shareit.item.dto.PostCommentRequest;
import ru.practicum.shareit.item.dto.PostItemRequest;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemControllerImpl implements ItemController {
    private final ItemClient itemClient;

    @Override
    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItem(@PathVariable long itemId) {
        log.info("Запрос на получение предмета с itemId: {}", itemId);
        return itemClient.getItem(itemId);
    }

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OwnedItemDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Запрос на получение списка предметов владельца c userId: {}", ownerId);
        return itemClient.getOwnerItems(ownerId);
    }

    @Override
    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Запрос на поиск предметов с совпадением в имени или описании. Text: {}", text);
        return itemClient.searchItems(text);
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody PostItemRequest request,
                              @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Запрос на добавление предмета владельцем c userId: {}. Request: {}", ownerId, request);
        return itemClient.createItem(request, ownerId);
    }

    @Override
    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto patchItem(@PathVariable long itemId,
                             @RequestBody PatchItemRequest request,
                             @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Запрос на изменение предмета с itemid: {} владельца с userId: {}. Request: {}",
                itemId, ownerId, request);
        return itemClient.patchItem(itemId, request, ownerId);
    }

    @Override
    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@RequestBody PostCommentRequest request,
                                 @PathVariable long itemId,
                                 @RequestHeader("X-Sharer-User-Id") long authorId) {
        log.info("Запрос на добавление комментария к itemId: {} от authorId: {}. Request: {}",
                itemId, authorId, request);
        return itemClient.addComment(request, authorId, itemId);
    }

    @Override
    @GetMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllCommentsForItem(@PathVariable long itemId) {
        log.info("Получен запрос на получение комментариев к предмету с itemId: {}", itemId);
        return itemClient.getAllCommentsForItem(itemId);
    }

    @Override
    @GetMapping("/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllCommentsForOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Получен запрос на получение комментариев владельца с ownerId: {}", ownerId);
        return itemClient.getAllCommentsForOwner(ownerId);
    }
}
