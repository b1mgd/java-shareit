package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Optional<Item> getItem(long itemId);

    List<Item> getItems(long ownerId);

    List<Item> searchItems(String text);

    Item saveItem(Item item);

    Item patchItem(Item item);
}
