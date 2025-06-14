package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Boolean existsByEmail(String email);
}
