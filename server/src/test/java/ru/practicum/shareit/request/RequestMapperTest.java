package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.dto.ShortItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class RequestMapperTest {
    @Autowired
    private RequestMapper requestMapper;

    @Test
    void mapToRequestDto_shouldMapAllFields() {
        Request request = new Request();
        request.setId(1L);
        request.setDescription("desc");
        User requestor = new User();
        requestor.setId(2L);
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        Item item = new Item();
        item.setId(3L);
        item.setName("item");
        item.setOwner(requestor);
        Set<Item> items = new HashSet<>();
        items.add(item);
        request.setItems(items);

        RequestDto dto = requestMapper.mapToRequestDto(request);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("desc");
        assertThat(dto.getRequestorId()).isEqualTo(2L);
        assertThat(dto.getCreated()).isNotNull();
        assertThat(dto.getItems()).isNotNull();
        assertThat(dto.getItems()).hasSize(1);
        ShortItem shortItem = dto.getItems().iterator().next();
        assertThat(shortItem.getId()).isEqualTo(3L);
        assertThat(shortItem.getName()).isEqualTo("item");
        assertThat(shortItem.getOwnerId()).isEqualTo(2L);
    }

    @Test
    void mapToRequestDto_shouldReturnNull_whenRequestIsNull() {
        assertThat(requestMapper.mapToRequestDto(null)).isNull();
    }

    @Test
    void mapToRequest_shouldMapAllFieldsAndSetRequestor() {
        PostRequestDto req = new PostRequestDto();
        req.setDescription("desc");
        User requestor = new User();
        requestor.setId(2L);

        Request request = requestMapper.mapToRequest(req, requestor);
        assertThat(request).isNotNull();
        assertThat(request.getDescription()).isEqualTo("desc");
        assertThat(request.getRequestor()).isEqualTo(requestor);
    }

    @Test
    void mapToRequest_shouldReturnNull_whenRequestIsNull() {
        assertThat(requestMapper.mapToRequest(null, new User())).isNull();
    }

    @Test
    void map_setOfItemsToShortItems() {
        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);
        Set<Item> items = new HashSet<>();
        items.add(item);
        Set<ShortItem> result = requestMapper.map(items);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        ShortItem shortItem = result.iterator().next();
        assertThat(shortItem.getId()).isEqualTo(1L);
        assertThat(shortItem.getName()).isEqualTo("item");
        assertThat(shortItem.getOwnerId()).isEqualTo(2L);
    }

    @Test
    void map_setOfItemsToShortItems_shouldReturnNull_whenNull() {
        assertThat(requestMapper.map(null)).isNull();
    }
}
