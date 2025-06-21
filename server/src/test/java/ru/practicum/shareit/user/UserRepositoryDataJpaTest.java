package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryDataJpaTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager em;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(null, "user", "user@email.com");
        em.persist(user);
        em.flush();
    }

    @Test
    @DisplayName("findById возвращает пользователя по id")
    void findById() {
        User found = userRepository.findById(user.getId()).orElse(null);
        assertThat(found).isEqualTo(user);
    }

    @Test
    @DisplayName("existsByEmail возвращает true для существующего email")
    void existsByEmail() {
        boolean exists = userRepository.existsByEmail(user.getEmail());
        assertThat(exists).isTrue();
    }
}
