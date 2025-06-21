package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RequestRepositoryDataJpaTest {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager em;

    private User requestor;

    private Request request;

    @BeforeEach
    void setUp() {
        requestor = new User(null, "requestor", "requestor@email.com");
        em.persist(requestor);
        request = new Request(null, "desc", requestor, null, null);
        request.setCreated(java.time.LocalDateTime.now());
        em.persist(request);
        em.flush();
    }

    @Test
    @DisplayName("findAllByRequestorId возвращает запросы пользователя")
    void findAllByRequestorId() {
        List<Request> requests = requestRepository.findAllByRequestorId(requestor.getId());
        assertThat(requests).isNotEmpty().contains(request);
    }

    @Test
    @DisplayName("findById возвращает запрос по id")
    void findById() {
        Request found = requestRepository.findById(request.getId()).orElse(null);
        assertThat(found).isEqualTo(request);
    }
}
