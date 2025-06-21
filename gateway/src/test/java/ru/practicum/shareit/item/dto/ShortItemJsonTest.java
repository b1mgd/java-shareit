package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ShortItemJsonTest {
    private final JacksonTester<ShortItem> json;

    @Test
    void testShortItem() throws Exception {
        ShortItem shortItem = new ShortItem(123L, "ShortItemName", 456L);

        var result = json.write(shortItem);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(123);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("ShortItemName");
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(456);
    }
}
