package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ShortItem;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestDtoJsonTest {
    private final JacksonTester<RequestDto> json;

    @Test
    void testRequestDto() throws Exception {
        LocalDateTime created = LocalDateTime.of(2025, 6, 18, 16, 0, 0);

        ShortItem shortItem1 = new ShortItem(1L, "ShortItem1", 10L);
        ShortItem shortItem2 = new ShortItem(2L, "ShortItem2", 20L);

        RequestDto requestDto = new RequestDto(
                100L,
                "Need a drill",
                50L,
                created,
                Set.of(shortItem1, shortItem2)
        );

        var result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(100);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Need a drill");
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(50);
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2025-06-18T16:00:00");

        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(2);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isIn(1, 2);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name")
                .isIn("ShortItem1", "ShortItem2");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isIn(10, 20);
    }
}
