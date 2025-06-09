package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.PostRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestControllerImpl implements RequestController {
    private final RequestService requestService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(PostRequestDto request,
                                    @RequestHeader("X-Sharer-User-Id") long requestorId) {
        log.info("Создание запроса на предмет от пользователя с id: {}. \nRequest: {}", requestorId, request);
        return requestService.createRequest(request, requestorId);
    }

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> findAllRequestsByRequestorId(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        log.info("Получение всех запросов на предметы пользователя с id: {}", requestorId);
        return requestService.findAllRequestsByRequestorId(requestorId);
    }

    @Override
    @GetMapping("/all")
    public List<RequestDto> findAllRequests() {
        log.info("Поиск всех существующих запросов на предметы от других пользователей");
        return requestService.findAllRequests();
    }

    @Override
    public RequestDto findRequestById(long id) {
        log.info("Поиск запроса на предмет по id: {}.", id);
        return requestService.findRequestById(id);
    }
}
