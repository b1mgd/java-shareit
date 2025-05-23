package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
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
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto getItem(long itemId) {
        return itemRepository.getItem(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Предмет с itemId " + itemId + " не был найден"));
    }

    @Override
    public List<ItemDto> getOwnerItems(long ownerId) {
        return itemRepository.getItems(ownerId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        text = text.toLowerCase().trim();
        return itemRepository.searchItems(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(PostItemRequest request, long ownerId) {
        User user = userRepository.getUser(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не был найден"));

        Item item = ItemMapper.toItem(request);
        item.setOwnerId(ownerId);

        Item savedItem = itemRepository.saveItem(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto patchItem(long itemId, PatchItemRequest request, long ownerId) {
        Item existingItem = itemRepository.getItem(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с itemId " + itemId + " не был найден"));

        if (existingItem.getOwnerId() != ownerId) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не является владельцем предмета");
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
        return ItemMapper.toItemDto(updatedItem);
    }
}
