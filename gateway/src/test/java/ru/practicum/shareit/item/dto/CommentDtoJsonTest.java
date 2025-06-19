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
public class CommentDtoJsonTest {
    private final JacksonTester<CommentDto> json;

    @Test
    void testCommentDto() throws Exception {
        LocalDateTime created = LocalDateTime.of(2025, 6, 18, 15, 30, 0);

        CommentDto comment = new CommentDto(
                1L,
                "Great item!",
                "Alice",
                created
        );

        var result = json.write(comment);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Great item!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Alice");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2025-06-18T15:30:00");
    }
}
