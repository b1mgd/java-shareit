package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OwnedItemDtoJsonTest {
    private final JacksonTester<OwnedItemDto> json;

    @Test
    void testOwnedItemDto() throws Exception {
        LocalDateTime lastStart = LocalDateTime.of(2025, 6, 15, 10, 0, 0);
        LocalDateTime lastEnd = LocalDateTime.of(2025, 6, 16, 10, 0, 0);
        LocalDateTime nextStart = LocalDateTime.of(2025, 6, 20, 10, 0, 0);
        LocalDateTime nextEnd = LocalDateTime.of(2025, 6, 21, 10, 0, 0);

        OwnedItemDto ownedItemDto = new OwnedItemDto();
        ownedItemDto.setId(100L);
        ownedItemDto.setName("OwnedItem");
        ownedItemDto.setDescription("Description of owned item");
        ownedItemDto.setAvailable(true);
        ownedItemDto.setOwnerId(200L);
        ownedItemDto.setRequestId(300L);
        ownedItemDto.setLastStart(lastStart);
        ownedItemDto.setLastEnd(lastEnd);
        ownedItemDto.setNextStart(nextStart);
        ownedItemDto.setNextEnd(nextEnd);

        var result = json.write(ownedItemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(100);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("OwnedItem");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Description of owned item");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(200);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(300);

        assertThat(result).extractingJsonPathStringValue("$.lastStart")
                .isEqualTo("2025-06-15T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.lastEnd")
                .isEqualTo("2025-06-16T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.nextStart")
                .isEqualTo("2025-06-20T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.nextEnd")
                .isEqualTo("2025-06-21T10:00:00");
    }
}
