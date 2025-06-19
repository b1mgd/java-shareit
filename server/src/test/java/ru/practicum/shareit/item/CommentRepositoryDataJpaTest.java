package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryDataJpaTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager em;

    private User author;
    private User owner;
    private Item item;
    private Comment comment;

    @BeforeEach
    void setUp() {
        owner = new User(null, "owner", "owner@email.com");
        author = new User(null, "author", "author@email.com");
        em.persist(owner);
        em.persist(author);
        item = new Item(null, "item", "desc", true, owner, null);
        em.persist(item);
        comment = new Comment(null, "text", item, author, LocalDateTime.now());
        em.persist(comment);
        em.flush();
    }

    @Test
    @DisplayName("findAllByItemId возвращает комментарии к предмету")
    void findAllByItemId() {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        assertThat(comments).isNotEmpty().contains(comment);
    }

    @Test
    @DisplayName("findAllByItemOwnerId возвращает комментарии к предметам владельца")
    void findAllByItemOwnerId() {
        List<Comment> comments = commentRepository.findAllByItemOwnerId(owner.getId());
        assertThat(comments).isNotEmpty().contains(comment);
    }

    @Test
    @DisplayName("findById возвращает комментарий по id")
    void findById() {
        Comment found = commentRepository.findById(comment.getId()).orElse(null);
        assertThat(found).isEqualTo(comment);
    }
}
