package ru.practicum.shareit.request;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(serverUrl, builder, API_PREFIX);
    }

    public RequestDto createRequest(PostRequestDto request, long requestorId) {
        ResponseEntity<RequestDto> response = post("", requestorId, request, RequestDto.class);
        return response.getBody();
    }

    public List<RequestDto> findAllRequestsByRequestorId(long requestorId) {
        ResponseEntity<List<RequestDto>> response = getList("", requestorId, RequestDto.class);
        return response.getBody();
    }

    public List<RequestDto> findAllRequests() {
        ResponseEntity<List<RequestDto>> response = getList("/all", RequestDto.class);
        return response.getBody();
    }

    public RequestDto findRequestById(long id) {
        ResponseEntity<RequestDto> response = get("/" + id, RequestDto.class);
        return response.getBody();
    }
}
