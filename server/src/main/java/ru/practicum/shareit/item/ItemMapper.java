package ru.practicum.shareit.item;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnedItemDto;
import ru.practicum.shareit.item.dto.PostItemRequest;
import ru.practicum.shareit.item.dto.ShortItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.request.model.Request;

@Mapper(componentModel = "spring", uses = {UserMapper.class, RequestMapper.class})
public interface ItemMapper {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    ItemDto mapToItemDto(Item item);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "request.id", target = "requestId")
    OwnedItemDto mapToOwnedItemDto(Item item);

    Item mapToItem(PostItemRequest request, @Context User owner, @Context Request itemRequest);

    @AfterMapping
    default void setOwnerAndRequest(PostItemRequest request,
                                    @MappingTarget Item item,
                                    @Context User owner,
                                    @Context Request itemRequest) {
        item.setOwner(owner);
        item.setRequest(itemRequest);
    }
}
