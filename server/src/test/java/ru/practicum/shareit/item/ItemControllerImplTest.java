package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.item.dto.OwnedItemDto;
import ru.practicum.shareit.item.dto.PatchItemRequest;
import ru.practicum.shareit.item.dto.PostCommentRequest;

import java.util.List;

import ru.practicum.shareit.item.dto.PostItemRequest;
import ru.practicum.shareit.item.dto.CommentDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemControllerImpl.class)
class ItemControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getItem_shouldReturnItem() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        Mockito.when(itemService.getItem(1L)).thenReturn(item);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getOwnerItems_shouldReturnList() throws Exception {
        OwnedItemDto ownedItem = new OwnedItemDto();
        ownedItem.setId(1L);
        Mockito.when(itemService.getOwnerItems(1L)).thenReturn(List.of(ownedItem));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void searchItems_shouldReturnList() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(2L);
        Mockito.when(itemService.searchItems("test")).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search").param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L));
    }

    @Test
    void createItem_shouldReturnCreatedItem() throws Exception {
        PostItemRequest request = new PostItemRequest();
        request.setName("item");
        ItemDto item = new ItemDto();
        item.setId(3L);
        Mockito.when(itemService.createItem(Mockito.any(), Mockito.eq(1L))).thenReturn(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L));
    }

    @Test
    void patchItem_shouldReturnPatchedItem() throws Exception {
        PatchItemRequest patchRequest = new PatchItemRequest();
        patchRequest.setName("patched");
        ItemDto item = new ItemDto();
        item.setId(4L);
        Mockito.when(itemService.patchItem(Mockito.eq(4L), Mockito.any(), Mockito.eq(1L))).thenReturn(item);

        mockMvc.perform(patch("/items/4")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4L));
    }

    @Test
    void addComment_shouldReturnComment() throws Exception {
        PostCommentRequest commentRequest = new PostCommentRequest();
        commentRequest.setText("Nice!");
        CommentDto comment = new CommentDto();
        comment.setId(5L);
        Mockito.when(itemService.addComment(Mockito.any(), Mockito.eq(4L), Mockito.eq(1L))).thenReturn(comment);

        mockMvc.perform(post("/items/4/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5L));
    }

    @Test
    void getAllCommentsForItem_shouldReturnList() throws Exception {
        CommentDto comment = new CommentDto();
        comment.setId(6L);
        Mockito.when(itemService.getAllCommentsForItem(4L)).thenReturn(List.of(comment));

        mockMvc.perform(get("/items/4/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(6L));
    }

    @Test
    void getAllCommentsForOwner_shouldReturnList() throws Exception {
        CommentDto comment = new CommentDto();
        comment.setId(7L);
        Mockito.when(itemService.getAllCommentsForOwner(1L)).thenReturn(List.of(comment));

        mockMvc.perform(get("/items/comments/owner").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7L));
    }
}
