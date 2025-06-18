package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.PostRequestDto;

import java.util.List;

public interface RequestController {

    RequestDto createRequest(PostRequestDto request, long requestorId);

    List<RequestDto> findAllRequestsByRequestorId(long requestorId);

    List<RequestDto> findAllRequests();

    RequestDto findRequestById(long id);
}
