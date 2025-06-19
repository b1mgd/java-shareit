package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import jakarta.validation.ConstraintViolationException;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExceptionHandlerTest.StubController.class)
@Import(ErrorHandler.class)
@DisplayName("Тестирование ErrorHandler в gateway через стаб-контроллер")
class ExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @RestController
    @RequestMapping("/stub-exception")
    static class StubController {
        @GetMapping("/method-arg-not-valid")
        public void methodArgNotValid(@RequestParam("name") @NotBlank String name) {
        }

        @PostMapping("/validate-dto")
        public void validateDto(@RequestBody @jakarta.validation.Valid TestDto dto) {
        }

        @GetMapping("/constraint-violation")
        public void constraintViolation() {
            throw new ConstraintViolationException("Ошибка валидации", null);
        }

        @GetMapping("/missing-header")
        public void missingHeader(@RequestHeader("X-Header") String header) {
        }

        @GetMapping("/validation-exception")
        public void validationException() {
            throw new ValidationException("Ошибка валидации через ValidationException");
        }

        @GetMapping("/type-mismatch")
        public void typeMismatch(@RequestParam("id") Integer id) {
        }

        @GetMapping("/http-status-code")
        public void httpStatusCode() {
            throw new org.springframework.web.client.HttpClientErrorException(org.springframework.http.HttpStatus.NOT_FOUND, "Not found");
        }

        @PostMapping("/not-readable")
        public void notReadable(@RequestBody String body) {
            throw new HttpMessageNotReadableException("Ошибка чтения");
        }

        @GetMapping("/unexpected")
        public void unexpected() {
            throw new RuntimeException("Неожиданная ошибка");
        }

        static class TestDto {
            @NotBlank(message = "Имя не должно быть пустым")
            public String name;
        }
    }

    @Nested
    @DisplayName("Обработка исключений")
    class ExceptionHandling {
        @Test
        @DisplayName("MethodArgumentNotValidException выбрасывается при невалидном DTO и возвращает 400")
        void handleMethodArgumentNotValid_withRealValidation() throws Exception {
            String invalidJson = objectMapper.writeValueAsString(new StubController.TestDto());
            mockMvc.perform(post("/stub-exception/validate-dto")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Ошибка валидации данных"));
        }

        @Test
        @DisplayName("ConstraintViolationException возвращает 400 и сообщение")
        void handleConstraintViolation() throws Exception {
            mockMvc.perform(get("/stub-exception/constraint-violation"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Ошибка валидации данных"));
        }

        @Test
        @DisplayName("MissingRequestHeaderException возвращает 400 и сообщение")
        void handleMissingHeader() throws Exception {
            mockMvc.perform(get("/stub-exception/missing-header"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Отсутствует обязательный заголовок запроса"));
        }

        @Test
        @DisplayName("ValidationException возвращает 400 и сообщение")
        void handleValidationException() throws Exception {
            mockMvc.perform(get("/stub-exception/validation-exception"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value(containsString("Ошибка валидации через ValidationException")));
        }

        @Test
        @DisplayName("MethodArgumentTypeMismatchException возвращает 400 и сообщение")
        void handleTypeMismatch() throws Exception {
            mockMvc.perform(get("/stub-exception/type-mismatch?id=abc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value(containsString("Unknown id: abc")));
        }

        @Test
        @DisplayName("HttpStatusCodeException возвращает статус и сообщение")
        void handleHttpStatusCodeException() throws Exception {
            mockMvc.perform(get("/stub-exception/http-status-code"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        @DisplayName("HttpMessageNotReadableException возвращает 400 и сообщение")
        void handleNotReadable() throws Exception {
            mockMvc.perform(post("/stub-exception/not-readable")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid_json}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Ошибка чтения тела запроса или невалидный формат JSON"));
        }

        @Test
        @DisplayName("Неожиданное исключение возвращает 500 и стандартное сообщение")
        void handleUnexpected() throws Exception {
            mockMvc.perform(get("/stub-exception/unexpected"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.error").value("Internal Server Error"));
        }
    }
}
