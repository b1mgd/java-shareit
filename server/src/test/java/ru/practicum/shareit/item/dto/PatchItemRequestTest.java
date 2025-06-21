package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PatchItemRequest: unit-тесты edge-cases")
class PatchItemRequestTest {

    @Nested
    @DisplayName("hasName")
    class HasName {

        @Test
        void nullName() {
            PatchItemRequest req = new PatchItemRequest();
            req.setName(null);
            assertThat(req.hasName()).isFalse();
        }

        @Test
        void emptyName() {
            PatchItemRequest req = new PatchItemRequest();
            req.setName("");
            assertThat(req.hasName()).isFalse();
        }

        @Test
        void blankName() {
            PatchItemRequest req = new PatchItemRequest();
            req.setName("   ");
            assertThat(req.hasName()).isFalse();
        }

        @Test
        void validName() {
            PatchItemRequest req = new PatchItemRequest();
            req.setName("item");
            assertThat(req.hasName()).isTrue();
        }
    }

    @Nested
    @DisplayName("hasDescription")
    class HasDescription {

        @Test
        void nullDescription() {
            PatchItemRequest req = new PatchItemRequest();
            req.setDescription(null);
            assertThat(req.hasDescription()).isFalse();
        }

        @Test
        void emptyDescription() {
            PatchItemRequest req = new PatchItemRequest();
            req.setDescription("");
            assertThat(req.hasDescription()).isFalse();
        }

        @Test
        void blankDescription() {
            PatchItemRequest req = new PatchItemRequest();
            req.setDescription("   ");
            assertThat(req.hasDescription()).isFalse();
        }

        @Test
        void validDescription() {
            PatchItemRequest req = new PatchItemRequest();
            req.setDescription("desc");
            assertThat(req.hasDescription()).isTrue();
        }
    }

    @Nested
    @DisplayName("hasAvailable")
    class HasAvailable {

        @Test
        void nullAvailable() {
            PatchItemRequest req = new PatchItemRequest();
            req.setAvailable(null);
            assertThat(req.hasAvailable()).isFalse();
        }

        @Test
        void trueAvailable() {
            PatchItemRequest req = new PatchItemRequest();
            req.setAvailable(true);
            assertThat(req.hasAvailable()).isTrue();
        }

        @Test
        void falseAvailable() {
            PatchItemRequest req = new PatchItemRequest();
            req.setAvailable(false);
            assertThat(req.hasAvailable()).isTrue();
        }
    }
}
