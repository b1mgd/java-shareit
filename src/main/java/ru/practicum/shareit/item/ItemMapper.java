package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnedItemDto;
import ru.practicum.shareit.item.dto.PostItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemRequestMapper.class})
public interface ItemMapper {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    ItemDto mapToItemDto(Item item);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "request.id", target = "requestId")
    OwnedItemDto mapToOwnedItemDto(Item item);

    Item mapToItem(PostItemRequest request);
}
