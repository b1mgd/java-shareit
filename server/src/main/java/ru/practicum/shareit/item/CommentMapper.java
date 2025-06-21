package ru.practicum.shareit.item;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.PostCommentRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "author.name", target = "authorName")
    CommentDto mapToCommentDto(Comment comment);

    Comment mapToComment(PostCommentRequest request, @Context Item item, @Context User author);

    @AfterMapping
    default void setItemAndAuthor(PostCommentRequest request,
                                  @MappingTarget Comment comment,
                                  @Context Item item,
                                  @Context User author) {
        comment.setItem(item);
        comment.setAuthor(author);
    }
}
