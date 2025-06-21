package ru.practicum.shareit.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ErrorHandlerTest.StubController.class)
@Import(ErrorHandler.class)
@DisplayName("Тестирование ErrorHandler через стаб-контроллер")
class ErrorHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    @RequestMapping("/stub-exception")
    static class StubController {
        @GetMapping("/not-found")
        public void notFound() {
            throw new NotFoundException("Объект не найден");
        }

        @GetMapping("/validation")
        public void validation() {
            throw new ValidationException("Ошибка валидации");
        }

        @GetMapping("/forbidden")
        public void forbidden() {
            throw new AccessForbiddenException("Доступ запрещён");
        }

        @GetMapping("/bad-request")
        public void badRequest() {
            throw new ArgumentsNotValidException("Некорректные аргументы");
        }

        @GetMapping("/unexpected")
        public void unexpected() {
            throw new RuntimeException("Неожиданная ошибка");
        }
    }

    @Nested
    @DisplayName("Обработка исключений")
    class ExceptionHandling {
        @Test
        @DisplayName("NotFoundException возвращает 404 и сообщение")
        void handleNotFound() throws Exception {
            mockMvc.perform(get("/stub-exception/not-found"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("Объект не найден")));
        }

        @Test
        @DisplayName("ValidationException возвращает 409 и сообщение")
        void handleValidation() throws Exception {
            mockMvc.perform(get("/stub-exception/validation"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(containsString("Ошибка валидации")));
        }

        @Test
        @DisplayName("AccessForbiddenException возвращает 403 и сообщение")
        void handleForbidden() throws Exception {
            mockMvc.perform(get("/stub-exception/forbidden"))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(containsString("Доступ запрещён")));
        }

        @Test
        @DisplayName("ArgumentsNotValidException возвращает 400 и сообщение")
        void handleArgumentsNotValid() throws Exception {
            mockMvc.perform(get("/stub-exception/bad-request"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("Некорректные аргументы")));
        }

        @Test
        @DisplayName("Неожиданное исключение возвращает 500 и стандартное сообщение")
        void handleUnexpected() throws Exception {
            mockMvc.perform(get("/stub-exception/unexpected"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("Internal Server Error"));
        }
    }
}
