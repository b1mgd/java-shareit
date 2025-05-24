package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemRequest;
import ru.practicum.shareit.item.dto.PostItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto getItem(long itemId) {
        ItemDto result = itemRepository.getItem(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Предмет с itemId " + itemId + " не был найден"));
        log.info("Получен результат: {}", result);
        return result;
    }

    @Override
    public List<ItemDto> getOwnerItems(long ownerId) {
        List<ItemDto> result = itemRepository.getItems(ownerId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Получен результат: {}", result);
        return result;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        text = text.toLowerCase().trim();
        List<ItemDto> result = itemRepository.searchItems(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Получен результат: {}", result);
        return result;
    }

    @Override
    public ItemDto createItem(PostItemRequest request, long ownerId) {
        User user = userRepository.getUser(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с userId " + ownerId + " не был найден"));

        Item item = ItemMapper.toItem(request);
        item.setOwnerId(ownerId);
        Item savedItem = itemRepository.saveItem(item);
        log.info("Получен результат: {}", savedItem);

        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto patchItem(long itemId, PatchItemRequest request, long ownerId) {
        Item existingItem = itemRepository.getItem(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с itemId " + itemId + " не был найден"));

        if (existingItem.getOwnerId() != ownerId) {
            throw new NotFoundException("Пользователь с userId " + ownerId + " не является владельцем предмета");
        }

        if (request.hasName()) {
            existingItem.setName(request.getName());
        }
        if (request.hasDescription()) {
            existingItem.setDescription(request.getDescription());
        }
        if (request.hasAvailable()) {
            existingItem.setAvailable(request.getAvailable());
        }

        Item updatedItem = itemRepository.patchItem(existingItem);
        log.info("Получен результат: {}", updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }
}
