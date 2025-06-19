package ru.practicum.shareit.request;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ShortItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface RequestMapper {

    @Mapping(source = "requestor.id", target = "requestorId")
    RequestDto mapToRequestDto(Request itemRequest);

    default Set<ShortItem> map(Set<Item> items) {
        if (items == null) return null;
        return items.stream()
                .map(item -> {
                    ShortItem dto = new ShortItem();
                    dto.setId(item.getId());
                    dto.setName(item.getName());
                    dto.setOwnerId(item.getOwner() != null ? item.getOwner().getId() : null);
                    return dto;
                })
                .collect(java.util.stream.Collectors.toSet());
    }

    Request mapToRequest(PostRequestDto request, @Context User requestor);

    @AfterMapping
    default void setRequestor(PostRequestDto request,
                              @MappingTarget Request itemRequest,
                              @Context User requestor) {
        itemRequest.setRequestor(requestor);
    }
}
