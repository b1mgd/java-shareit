package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    @Override
    public Optional<Item> getItem(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getItems(long ownerId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwnerId() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String searchText = text.toLowerCase().trim();
        return items.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> {
                    String name = item.getName();
                    String description = item.getDescription();
                    return (name != null && name.toLowerCase().contains(searchText)) ||
                            (description != null && description.toLowerCase().contains(searchText));
                })
                .collect(Collectors.toList());
    }

    @Override
    public Item saveItem(Item item) {
        item.setId(idCounter.incrementAndGet());
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item patchItem(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }
}
