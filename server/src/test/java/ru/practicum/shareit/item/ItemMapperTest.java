package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnedItemDto;
import ru.practicum.shareit.item.dto.PostItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ItemMapperTest {
    @Autowired
    private ItemMapper itemMapper;

    @Test
    void mapToItemDto_shouldMapAllFields() {
        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);
        Request request = new Request();
        request.setId(3L);
        item.setRequest(request);

        ItemDto dto = itemMapper.mapToItemDto(item);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("item");
        assertThat(dto.getDescription()).isEqualTo("desc");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getOwner()).isNotNull();
        assertThat(dto.getOwner().getId()).isEqualTo(2L);
        assertThat(dto.getRequest()).isNotNull();
        assertThat(dto.getRequest().getId()).isEqualTo(3L);
    }

    @Test
    void mapToOwnedItemDto_shouldMapOwnerAndRequestIds() {
        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);
        Request request = new Request();
        request.setId(3L);
        item.setRequest(request);

        OwnedItemDto dto = itemMapper.mapToOwnedItemDto(item);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getOwnerId()).isEqualTo(2L);
        assertThat(dto.getRequestId()).isEqualTo(3L);
    }

    @Test
    void mapToItem_shouldMapAllFieldsAndSetOwnerAndRequest() {
        PostItemRequest req = new PostItemRequest();
        req.setName("item");
        req.setDescription("desc");
        req.setAvailable(true);
        User owner = new User();
        owner.setId(2L);
        Request request = new Request();
        request.setId(3L);

        Item item = itemMapper.mapToItem(req, owner, request);
        assertThat(item).isNotNull();
        assertThat(item.getName()).isEqualTo("item");
        assertThat(item.getDescription()).isEqualTo("desc");
        assertThat(item.isAvailable()).isTrue();
        assertThat(item.getOwner()).isEqualTo(owner);
        assertThat(item.getRequest()).isEqualTo(request);
    }

    @Test
    void mapToItemDto_shouldReturnNull_whenItemIsNull() {
        assertThat(itemMapper.mapToItemDto(null)).isNull();
    }

    @Test
    void mapToOwnedItemDto_shouldReturnNull_whenItemIsNull() {
        assertThat(itemMapper.mapToOwnedItemDto(null)).isNull();
    }
}
