package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.PostCommentRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CommentMapperTest {

    @Autowired
    private CommentMapper commentMapper;

    @Test
    void mapToCommentDto_shouldMapAllFields() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("text");
        User author = new User();
        author.setId(2L);
        author.setName("author");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        CommentDto dto = commentMapper.mapToCommentDto(comment);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("text");
        assertThat(dto.getAuthorName()).isEqualTo("author");
        assertThat(dto.getCreated()).isNotNull();
    }

    @Test
    void mapToCommentDto_shouldReturnNull_whenCommentIsNull() {
        assertThat(commentMapper.mapToCommentDto(null)).isNull();
    }

    @Test
    void mapToComment_shouldMapAllFieldsAndSetItemAndAuthor() {
        PostCommentRequest req = new PostCommentRequest();
        req.setText("text");
        Item item = new Item();
        item.setId(3L);
        User author = new User();
        author.setId(2L);

        Comment comment = commentMapper.mapToComment(req, item, author);
        assertThat(comment).isNotNull();
        assertThat(comment.getText()).isEqualTo("text");
        assertThat(comment.getItem()).isEqualTo(item);
        assertThat(comment.getAuthor()).isEqualTo(author);
    }

    @Test
    void mapToComment_shouldReturnNull_whenRequestIsNull() {
        assertThat(commentMapper.mapToComment(null, new Item(), new User())).isNull();
    }
}
