package ru.practicum.shareit.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected BaseClient(@Value("${shareit-server.url}") String serverUrl,
                        RestTemplateBuilder builder,
                        String apiPrefix) {
        this(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + apiPrefix))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    protected <T> ResponseEntity<T> get(String path, Class<T> responseType) {
        return get(path, null, null, responseType);
    }

    protected <T> ResponseEntity<T> get(String path, long userId, Class<T> responseType) {
        return get(path, userId, null, responseType);
    }

    protected <T> ResponseEntity<T> get(String path, Long userId, @Nullable Map<String, Object> parameters, Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null, responseType);
    }

    protected <T> ResponseEntity<List<T>> getList(String path, Class<T> responseType) {
        return getList(path, null, null, responseType);
    }

    protected <T> ResponseEntity<List<T>> getList(String path, long userId, Class<T> responseType) {
        return getList(path, userId, null, responseType);
    }

    protected <T> ResponseEntity<List<T>> getList(String path, Long userId, @Nullable Map<String, Object> parameters, Class<T> responseType) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null, new ParameterizedTypeReference<List<T>>() {
        });
    }

    protected <T, R> ResponseEntity<R> post(String path, T body, Class<R> responseType) {
        return post(path, null, null, body, responseType);
    }

    protected <T, R> ResponseEntity<R> post(String path, long userId, T body, Class<R> responseType) {
        return post(path, userId, null, body, responseType);
    }

    protected <T, R> ResponseEntity<R> post(String path, Long userId, @Nullable Map<String, Object> parameters, T body, Class<R> responseType) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body, responseType);
    }

    protected <T, R> ResponseEntity<R> patch(String path, T body, Class<R> responseType) {
        return patch(path, null, null, body, responseType);
    }

    protected <T, R> ResponseEntity<R> patch(String path, long userId, T body, Class<R> responseType) {
        return patch(path, userId, null, body, responseType);
    }

    protected <T, R> ResponseEntity<R> patch(String path, Long userId, @Nullable Map<String, Object> parameters, T body, Class<R> responseType) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body, responseType);
    }

    protected <T, R> ResponseEntity<R> put(String path, long userId, T body, Class<R> responseType) {
        return put(path, userId, null, body, responseType);
    }

    protected <T, R> ResponseEntity<R> put(String path, long userId, @Nullable Map<String, Object> parameters, T body, Class<R> responseType) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, parameters, body, responseType);
    }

    protected ResponseEntity<Void> delete(String path) {
        return delete(path, null, null);
    }

    protected ResponseEntity<Void> delete(String path, long userId) {
        return delete(path, userId, null);
    }

    protected ResponseEntity<Void> delete(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null, Void.class);
    }

    private <T, R> ResponseEntity<R> makeAndSendRequest(HttpMethod method,
                                                        String path,
                                                        Long userId,
                                                        @Nullable Map<String, Object> parameters,
                                                        @Nullable T body,
                                                        Class<R> responseType) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<R> shareitServerResponse;
        try {
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, responseType, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, responseType);
            }
        } catch (HttpStatusCodeException e) {
            throw e;
        }
        return prepareGatewayResponse(shareitServerResponse);
    }

    private <T, R> ResponseEntity<R> makeAndSendRequest(HttpMethod method,
                                                        String path,
                                                        Long userId,
                                                        @Nullable Map<String, Object> parameters,
                                                        @Nullable T body,
                                                        ParameterizedTypeReference<R> responseType) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<R> shareitServerResponse;
        try {
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, responseType, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, responseType);
            }
        } catch (HttpStatusCodeException e) {
            throw e;
        }
        return prepareGatewayResponse(shareitServerResponse);
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    private static <R> ResponseEntity<R> prepareGatewayResponse(ResponseEntity<R> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
