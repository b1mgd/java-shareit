package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingJpaRepository;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ArgumentsNotValidException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemJpaRepository itemRepository;
    private final UserJpaRepository userRepository;
    private final BookingJpaRepository bookingRepository;
    private final CommentJpaRepository commentRepository;

    @Override
    public ItemDto getItem(long itemId) {
        ItemDto result = ItemMapper.mapToItemDto(getItemById(itemId));
        result.setComments(getAllCommentsForItem(itemId));
        log.info("Получен результат: {}", result);

        return result;
    }

    @Override
    public List<OwnedItemDto> getOwnerItems(long ownerId) {
        User owner = getUser(ownerId);
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Booking> bookings = bookingRepository.findAllByItemIdIn(itemIds);

        List<OwnedItemDto> result = new ArrayList<>();

        for (Item item : items) {
            OwnedItemDto ownedItemDto = ItemMapper.mapToOwnedItemDto(item, bookings);
            result.add(ownedItemDto);
        }

        log.info("Получен результат для владельца {}: {}", ownerId, result);
        return result;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        List<ItemDto> result = itemRepository.searchItems(text)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
        log.info("Получен результат: {}", result);

        return result;
    }

    @Override
    public ItemDto createItem(PostItemRequest request, long ownerId) {
        User owner = getUser(ownerId);

        Item item = ItemMapper.mapToItem(request);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);
        log.info("Получен результат: {}", savedItem);

        return ItemMapper.mapToItemDto(savedItem);
    }

    @Override
    public ItemDto patchItem(long itemId, PatchItemRequest request, long ownerId) {
        Item existingItem = getItemById(itemId);

        if (existingItem.getOwner().getId() != ownerId) {
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

        Item updatedItem = itemRepository.save(existingItem);
        log.info("Получен результат: {}", updatedItem);

        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public CommentDto addComment(PostCommentRequest request, long itemId, Long authorId) {
        User author = getUser(authorId);
        Item item = getItemById(itemId);

        if (item.getOwner().getId().equals(authorId)) {
            throw new ArgumentsNotValidException("Владелец не может оставлять комментарии к своей вещи");
        }

        Optional<Booking> booking = bookingRepository
                .findFirstByItemIdAndBookerIdAndStatusAndEndBefore(itemId, authorId, Status.APPROVED, LocalDateTime.now());

        if (booking.isEmpty()) {
            throw new ArgumentsNotValidException("Пользователь с authorId " + authorId +
                    " не может оставить комментарий к предмету c itemId , " +
                    itemId + " т.к. не имеет завершенного подтвержденного бронирования");
        }

        Comment comment = CommentMapper.mapToComment(request, item, author);
        Comment savedComment = commentRepository.save(comment);

        CommentDto commentDto = CommentMapper.mapToCommentDto(savedComment);
        log.info("Комментарий сохранен: {}", commentDto);

        return commentDto;
    }


    @Override
    public List<CommentDto> getAllCommentsForItem(long itemId) {
        List<CommentDto> comments = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
        log.info("Получены комментарии: {}", comments);

        return comments;
    }

    @Override
    public List<CommentDto> getAllCommentsForOwner(long ownerId) {
        List<CommentDto> comments = commentRepository.findAllByItemOwnerId(ownerId)
                .stream()
                .map(CommentMapper::mapToCommentDto)
                .collect(Collectors.toList());
        log.info("Получены комментарии к предметам владельца: {}", comments);

        return comments;
    }

    private User getUser(long ownerId) {
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с userId " + ownerId + " не был найден"));
    }

    private Item getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с itemId " + itemId + " не был найден"));
    }
}
