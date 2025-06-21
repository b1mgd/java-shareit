package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemControllerImpl implements ItemController {
    private final ItemService itemService;

    @Override
    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItem(@PathVariable long itemId) {
        log.info("Запрос на получение предмета с itemId: {}", itemId);
        return itemService.getItem(itemId);
    }

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OwnedItemDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Запрос на получение списка предметов владельца c userId: {}", ownerId);
        return itemService.getOwnerItems(ownerId);
    }

    @Override
    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Запрос на поиск предметов с совпадением в имени или описании. Text: {}", text);
        return itemService.searchItems(text);
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody PostItemRequest request,
                              @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Запрос на добавление предмета владельцем c userId: {}. Request: {}", ownerId, request);
        return itemService.createItem(request, ownerId);
    }

    @Override
    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto patchItem(@PathVariable long itemId,
                             @RequestBody PatchItemRequest request,
                             @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Запрос на изменение предмета с itemid: {} владельца с userId: {}. Request: {}",
                itemId, ownerId, request);
        return itemService.patchItem(itemId, request, ownerId);
    }

    @Override
    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@RequestBody PostCommentRequest request,
                                 @PathVariable long itemId,
                                 @RequestHeader("X-Sharer-User-Id") long authorId) {
        log.info("Запрос на добавление комментария к itemId: {} от authorId: {}. Request: {}",
                itemId, authorId, request);
        return itemService.addComment(request, itemId, authorId);
    }

    @Override
    @GetMapping("/{itemId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllCommentsForItem(@PathVariable long itemId) {
        log.info("Получен запрос на получение комментариев к предмету с itemId: {}", itemId);
        return itemService.getAllCommentsForItem(itemId);
    }

    @Override
    @GetMapping("/comments/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllCommentsForOwner(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.getAllCommentsForOwner(ownerId);
    }
}
