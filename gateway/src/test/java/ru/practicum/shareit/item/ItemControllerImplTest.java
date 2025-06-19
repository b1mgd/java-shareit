package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpStatusCodeException;
import ru.practicum.shareit.item.dto.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemControllerImpl.class)
public class ItemControllerImplTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MockMvc mockMvc;

    private final ItemDto itemDto = new ItemDto(1L, "item", "desc", true, null, null, null, null, List.of());
    private final OwnedItemDto ownedItemDto = new OwnedItemDto();
    private final PostItemRequest validItemRequest = new PostItemRequest();
    private final PatchItemRequest patchItemRequest = new PatchItemRequest();
    private final CommentDto commentDto = new CommentDto(1L, "text", "author", LocalDateTime.now());
    private final PostCommentRequest postCommentRequest = new PostCommentRequest();

    @Test
    void getItem_whenExists_shouldReturnOk() throws Exception {
        when(itemClient.getItem(anyLong())).thenReturn(itemDto);
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getOwnerItems_whenValid_shouldReturnOk() throws Exception {
        when(itemClient.getOwnerItems(anyLong())).thenReturn(List.of(ownedItemDto));
        mockMvc.perform(get("/items").header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getOwnerItems_withoutHeader_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchItems_whenTextProvided_shouldReturnOk() throws Exception {
        when(itemClient.searchItems(anyString())).thenReturn(List.of(itemDto));
        mockMvc.perform(get("/items/search").param("text", "item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void createItem_whenValid_shouldReturnCreated() throws Exception {
        when(itemClient.createItem(any(PostItemRequest.class), anyLong())).thenReturn(itemDto);
        validItemRequest.setName("item");
        validItemRequest.setDescription("desc");
        validItemRequest.setAvailable(true);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(validItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createItem_withoutHeader_shouldReturnBadRequest() throws Exception {
        validItemRequest.setName("item");
        validItemRequest.setDescription("desc");
        validItemRequest.setAvailable(true);
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(validItemRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchItem_whenValid_shouldReturnOk() throws Exception {
        when(itemClient.patchItem(anyLong(), any(PatchItemRequest.class), anyLong())).thenReturn(itemDto);
        patchItemRequest.setName("item");
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(patchItemRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void patchItem_withoutHeader_shouldReturnBadRequest() throws Exception {
        patchItemRequest.setName("item");
        mockMvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(patchItemRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_whenValid_shouldReturnCreated() throws Exception {
        when(itemClient.addComment(any(PostCommentRequest.class), anyLong(), anyLong())).thenReturn(commentDto);
        postCommentRequest.setText("text");
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(postCommentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void addComment_withoutHeader_shouldReturnBadRequest() throws Exception {
        postCommentRequest.setText("text");
        mockMvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(postCommentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllCommentsForItem_shouldReturnOk() throws Exception {
        when(itemClient.getAllCommentsForItem(anyLong())).thenReturn(List.of(commentDto));
        mockMvc.perform(get("/items/1/comment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllCommentsForOwner_shouldReturnOk() throws Exception {
        when(itemClient.getAllCommentsForOwner(anyLong())).thenReturn(List.of(commentDto));
        mockMvc.perform(get("/items/comments").header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllCommentsForOwner_withoutHeader_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items/comments"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_whenInternalError_shouldReturnServerError() throws Exception {
        when(itemClient.createItem(any(PostItemRequest.class), anyLong()))
                .thenThrow(new HttpStatusCodeException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error") {});
        validItemRequest.setName("item");
        validItemRequest.setDescription("desc");
        validItemRequest.setAvailable(true);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(validItemRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createItem_withEmptyBody_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
