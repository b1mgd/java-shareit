package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {
    private final JacksonTester<BookingDto> json;

    @Test
    void testBookingDtoBasicFieldsSerialization() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 6, 18, 12, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 6, 19, 12, 0, 0);

        BookingDto bookingDto = new BookingDto(
                1L,
                start,
                end,
                null,
                null,
                Status.APPROVED
        );

        var result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-06-18T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-06-19T12:00:00");

        assertThat(result).extractingJsonPathValue("$.item").isNull();
        assertThat(result).extractingJsonPathValue("$.booker").isNull();

        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }
}
