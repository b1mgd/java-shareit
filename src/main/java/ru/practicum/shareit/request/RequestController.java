package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.PostRequestDto;

import java.util.List;

public interface RequestController {

    RequestDto createRequest(@Valid PostRequestDto request, @Positive long requestorId);

    List<RequestDto> findAllRequestsByRequestorId(@Positive long requestorId);

    List<RequestDto> findAllRequests();

    RequestDto findRequestById(@Positive long id);
}
