package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTest {
    private final JacksonTester<ItemDto> json;

    @Test
    void testItemDtoCommentsSerialization() throws Exception {
        CommentDto comment1 = new CommentDto(
                1L,
                "First comment",
                "User1",
                LocalDateTime.of(2025, 6, 18, 10, 0, 0)
        );

        CommentDto comment2 = new CommentDto(
                2L,
                "Second comment",
                "User2",
                LocalDateTime.of(2025, 6, 18, 11, 0, 0)
        );

        ItemDto itemDto = new ItemDto(
                1L,
                "Item Name",
                "Item Description",
                true,
                null,
                null,
                null,
                null,
                List.of(comment1, comment2)  // comments
        );

        var result = json.write(itemDto);

        assertThat(result).extractingJsonPathArrayValue("$.comments").isNotEmpty();
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(2);

        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo("First comment");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo("User1");

        assertThat(result).extractingJsonPathNumberValue("$.comments[1].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.comments[1].text")
                .isEqualTo("Second comment");
        assertThat(result).extractingJsonPathStringValue("$.comments[1].authorName")
                .isEqualTo("User2");
    }
}
