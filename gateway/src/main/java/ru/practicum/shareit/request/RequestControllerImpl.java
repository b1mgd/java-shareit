package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestControllerImpl implements RequestController {
    private final RequestClient requestClient;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@RequestBody PostRequestDto request,
                                    @RequestHeader("X-Sharer-User-Id") long requestorId) {
        log.info("Создание запроса на предмет от пользователя с id: {}. \nRequest: {}", requestorId, request);
        return requestClient.createRequest(request, requestorId);
    }

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> findAllRequestsByRequestorId(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        log.info("Получение всех запросов на предметы пользователя с id: {}", requestorId);
        return requestClient.findAllRequestsByRequestorId(requestorId);
    }

    @Override
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> findAllRequests() {
        log.info("Поиск всех существующих запросов на предметы от других пользователей");
        return requestClient.findAllRequests();
    }

    @Override
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto findRequestById(@PathVariable long id) {
        log.info("Поиск запроса на предмет по id: {}.", id);
        return requestClient.findRequestById(id);
    }
}
