package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ArgumentsNotValidException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto getItem(long itemId) {
        ItemDto result = itemMapper.mapToItemDto(getItemById(itemId));
        result.setComments(getAllCommentsForItem(itemId));
        log.info("Получен результат: {}", result);

        return result;
    }

    @Override
    public List<OwnedItemDto> getOwnerItems(long ownerId) {
        validateUser(ownerId);
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Booking> bookings = bookingRepository.findAllByItemIdIn(itemIds);

        Map<Long, List<Booking>> bookingsByItemId = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        List<OwnedItemDto> result = new ArrayList<>();

        for (Item item : items) {
            OwnedItemDto ownedItemDto = itemMapper.mapToOwnedItemDto(item);
            List<Booking> itemBookings = bookingsByItemId.getOrDefault(item.getId(), Collections.emptyList());
            fillBookingDates(ownedItemDto, itemBookings);

            result.add(ownedItemDto);
        }

        log.info("Получен результат для владельца {}: {}", ownerId, result);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        List<ItemDto> result = itemRepository.searchItems(text)
                .stream()
                .map(itemMapper::mapToItemDto)
                .collect(Collectors.toList());
        log.info("Получен результат: {}", result);

        return result;
    }

    @Override
    public ItemDto createItem(PostItemRequest request, long ownerId) {
        User owner = getUser(ownerId);
        Long requestId = request.getRequestId();
        Request itemRequest = null;

        if (requestId != null) {
            itemRequest = getRequestById(requestId);
        }

        Item item = itemMapper.mapToItem(request, owner, itemRequest);
        Item savedItem = itemRepository.save(item);
        log.info("Получен результат: {}", savedItem);

        return itemMapper.mapToItemDto(savedItem);
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

        return itemMapper.mapToItemDto(updatedItem);
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

        Comment comment = commentMapper.mapToComment(request, item, author);
        Comment savedComment = commentRepository.save(comment);

        CommentDto commentDto = commentMapper.mapToCommentDto(savedComment);
        log.info("Комментарий сохранен: {}", commentDto);

        return commentDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllCommentsForItem(long itemId) {
        List<CommentDto> comments = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(commentMapper::mapToCommentDto)
                .collect(Collectors.toList());
        log.info("Получены комментарии: {}", comments);

        return comments;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllCommentsForOwner(long ownerId) {
        List<CommentDto> comments = commentRepository.findAllByItemOwnerId(ownerId)
                .stream()
                .map(commentMapper::mapToCommentDto)
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

    private Request getRequestById(long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на предмет с requestId " + requestId + " не был найден"));
    }

    private void validateUser(long userId) {
        boolean exists = userRepository.existsById(userId);

        if (!exists) {
            throw new NotFoundException("Пользователь с userId " + userId + " не был найден");
        }
    }

    private void fillBookingDates(OwnedItemDto dto, List<Booking> bookings) {
        if (bookings.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        bookings.stream()
                .filter(b -> b.getEnd().isBefore(now))
                .max(Comparator.comparing(Booking::getEnd))
                .ifPresent(lastBooking -> {
                    dto.setLastStart(lastBooking.getStart());
                    dto.setLastEnd(lastBooking.getEnd());
                });

        bookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .ifPresent(nextBooking -> {
                    dto.setNextStart(nextBooking.getStart());
                    dto.setNextEnd(nextBooking.getEnd());
                });
    }
}
