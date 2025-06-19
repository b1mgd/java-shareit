package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ItemClientTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private RestTemplateBuilder builder;
    private ItemClient itemClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(builder.uriTemplateHandler(any())).thenReturn(builder);
        when(builder.requestFactory(any(java.util.function.Supplier.class))).thenReturn(builder);
        when(builder.build()).thenReturn(restTemplate);
        itemClient = new ItemClient("http://localhost", builder);
    }

    @Test
    void getItem_success() {
        ItemDto expected = new ItemDto(1L, "name", "desc", true, null, null, null, null, null);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), eq(ItemDto.class)))
                .thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));
        ItemDto result = itemClient.getItem(1L);
        assertEquals(expected, result);
    }

    @Test
    void getOwnerItems_success() {
        OwnedItemDto owned = new OwnedItemDto();
        List<OwnedItemDto> expected = List.of(owned);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), any(org.springframework.core.ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity(expected, HttpStatus.OK));
        List<OwnedItemDto> result = itemClient.getOwnerItems(1L);
        assertEquals(expected, result);
    }

    @Test
    void searchItems_success() {
        ItemDto item = new ItemDto(1L, "name", "desc", true, null, null, null, null, null);
        List<ItemDto> expected = List.of(item);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), any(org.springframework.core.ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity(expected, HttpStatus.OK));
        List<ItemDto> result = itemClient.searchItems("item");
        assertEquals(expected, result);
    }

    @Test
    void createItem_success() {
        PostItemRequest req = new PostItemRequest();
        ItemDto expected = new ItemDto(1L, "name", "desc", true, null, null, null, null, null);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.POST), any(), eq(ItemDto.class)))
                .thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));
        ItemDto result = itemClient.createItem(req, 1L);
        assertEquals(expected, result);
    }

    @Test
    void patchItem_success() {
        PatchItemRequest req = new PatchItemRequest();
        ItemDto expected = new ItemDto(1L, "name", "desc", true, null, null, null, null, null);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.PATCH), any(), eq(ItemDto.class)))
                .thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));
        ItemDto result = itemClient.patchItem(1L, req, 1L);
        assertEquals(expected, result);
    }

    @Test
    void addComment_success() {
        PostCommentRequest req = new PostCommentRequest();
        CommentDto expected = new CommentDto(1L, "text", "author", null);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.POST), any(), eq(CommentDto.class)))
                .thenReturn(new ResponseEntity<>(expected, HttpStatus.OK));
        CommentDto result = itemClient.addComment(req, 1L, 1L);
        assertEquals(expected, result);
    }

    @Test
    void getAllCommentsForItem_success() {
        CommentDto comment = new CommentDto(1L, "text", "author", null);
        List<CommentDto> expected = List.of(comment);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), any(org.springframework.core.ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity(expected, HttpStatus.OK));
        List<CommentDto> result = itemClient.getAllCommentsForItem(1L);
        assertEquals(expected, result);
    }

    @Test
    void getAllCommentsForOwner_success() {
        CommentDto comment = new CommentDto(1L, "text", "author", null);
        List<CommentDto> expected = List.of(comment);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), any(org.springframework.core.ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity(expected, HttpStatus.OK));
        List<CommentDto> result = itemClient.getAllCommentsForOwner(1L);
        assertEquals(expected, result);
    }

    @Test
    void createItem_shouldThrowOnError() {
        PostItemRequest req = new PostItemRequest();
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.POST), any(), eq(ItemDto.class)))
                .thenThrow(mock(HttpStatusCodeException.class));
        assertThrows(HttpStatusCodeException.class, () -> itemClient.createItem(req, 1L));
    }

    @Test
    void getItem_non2xxWithBody_returnsBody() {
        ItemDto expected = new ItemDto(1L, "name", "desc", true, null, null, null, null, null);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), eq(ItemDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(expected));
        ItemDto result = itemClient.getItem(1L);
        assertEquals(expected, result);
    }

    @Test
    void getItem_non2xxWithoutBody_returnsNoBody() {
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), any(), eq(ItemDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        ItemDto result = itemClient.getItem(1L);
        assertNull(result);
    }
} 