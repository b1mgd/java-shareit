package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemRequest;
import ru.practicum.shareit.item.dto.PostItemRequest;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemControllerImpl implements ItemController {
    private final ItemService itemService;

    @Override
    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItem(@PathVariable long itemId) {
        return itemService.getItem(itemId);
    }

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.getOwnerItems(ownerId);
    }

    @Override
    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody @Valid PostItemRequest request,
                              @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.createItem(request, ownerId);
    }

    @Override
    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto patchItem(@PathVariable long itemId,
                             @RequestBody PatchItemRequest request,
                             @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.patchItem(itemId, request, ownerId);
    }
}
