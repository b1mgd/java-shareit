package ru.practicum.shareit.request;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PostCommentRequest;
import ru.practicum.shareit.item.dto.ShortItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface RequestMapper {

    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    @Mapping(source = "requestor.id", target = "requestorId")
    @Mapping(source = "items", target = "itemsDto")
    RequestDto mapToRequestDto(Request itemRequest);

    Set<ShortItem> map(Set<Item> items);

    Request mapToRequest(PostRequestDto request, User requestor);

    @AfterMapping
    default void setRequestor(PostCommentRequest request,
                              @MappingTarget Request itemRequest,
                              @Context User requestor) {
        itemRequest.setRequestor(requestor);
    }
}
