package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnedItemDto;
import ru.practicum.shareit.item.dto.PostItemRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());

        if (item.getOwner() != null) {
            itemDto.setOwnerId(item.getOwner().getId());
        }

        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }

        return itemDto;
    }

    public static OwnedItemDto mapToOwnedItemDto(Item item, List<Booking> bookings) {
        OwnedItemDto ownedItemDto = new OwnedItemDto();

        ownedItemDto.setId(item.getId());
        ownedItemDto.setName(item.getName());
        ownedItemDto.setDescription(item.getDescription());
        ownedItemDto.setAvailable(item.isAvailable());

        if (item.getOwner() != null) {
            ownedItemDto.setOwnerId(item.getOwner().getId());
        }

        if (item.getRequest() != null) {
            ownedItemDto.setRequestId(item.getRequest().getId());
        }

        bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());

        return ownedItemDto;
    }

    public static Item mapToItem(PostItemRequest request) {
        Item item = new Item();

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        item.setRequest(request.getRequest());

        return item;
    }
}
