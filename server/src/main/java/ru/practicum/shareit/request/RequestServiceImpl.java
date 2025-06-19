package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;

    @Override
    public RequestDto createRequest(PostRequestDto request, long requestorId) {
        User requestor = getUserById(requestorId);

        Request itemRequest = requestMapper.mapToRequest(request, requestor);
        Request savedRequest = requestRepository.save(itemRequest);
        RequestDto requestDto = requestMapper.mapToRequestDto(savedRequest);
        log.info("Запрос на предмет сохранен: {}", requestDto);

        return requestDto;
    }

    @Override
    public List<RequestDto> findAllRequestsByRequestorId(long requestorId) {
        validateRequestor(requestorId);
        List<RequestDto> requests = requestRepository.findAllByRequestorId(requestorId)
                .stream()
                .sorted(Comparator.comparing(Request::getCreated).reversed())
                .map(requestMapper::mapToRequestDto)
                .collect(Collectors.toList());
        log.info("Получены запросы на предметы пользователя с id: {}. \nRequests: {}", requestorId, requests);

        return requests;
    }

    @Override
    public List<RequestDto> findAllRequests() {
        List<RequestDto> requests = requestRepository.findAll()
                .stream()
                .sorted((Comparator.comparing(Request::getCreated).reversed()))
                .map(requestMapper::mapToRequestDto)
                .collect(Collectors.toList());
        log.info("Получен список запросов всех пользователей: {}", requests);

        return requests;
    }

    @Override
    public RequestDto findRequestById(long id) {
        Request request = getRequestById(id);
        RequestDto requestDto = requestMapper.mapToRequestDto(request);
        log.info("Получен запрос вещи с возможными вариантами аренды: {}", requestDto);

        return requestDto;
    }

    private User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: '%s' " +
                        "не зарегистрирован в системе", id)));
    }

    private Request getRequestById(long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос на предмет с id: '%s' не был найден", id)));
    }

    private void validateRequestor(long requestorId) {
        boolean exists = userRepository.existsById(requestorId);

        if (!exists) {
            throw new ValidationException(String.format("Пользователь с id: '%s' не зарегистрирован в системе",
                    requestorId));
        }
    }
}
