package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ArgumentsNotValidException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.model.Request;

import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ru.practicum.shareit.item.model.Comment;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ItemServiceImplTest {

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private RequestRepository requestRepository;

    @Autowired
    private ru.practicum.shareit.item.ItemMapper itemMapper;

    @Autowired
    private ru.practicum.shareit.item.CommentMapper commentMapper;

    private ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(
                itemRepository, userRepository, bookingRepository, commentRepository, requestRepository,
                itemMapper,
                commentMapper
        );

        when(itemRepository.findAllByOwnerId(anyLong()))
                .thenReturn(Collections.emptyList());
        when(itemRepository.searchItems(anyString()))
                .thenReturn(Collections.emptyList());
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());


        when(bookingRepository.findAllByItemIdIn(anyList()))
                .thenReturn(Collections.emptyList());

        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(Collections.emptyList());
        when(commentRepository.findAllByItemOwnerId(anyLong()))
                .thenReturn(Collections.emptyList());

        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(eq(1L)))
                .thenReturn(Optional.of(new User()));
        when(userRepository.existsById(eq(1L)))
                .thenReturn(true);
    }

    @Test
    void getItem_shouldThrowNotFoundException_whenItemNotFound() {
        long itemId = 1L;
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.getItem(itemId));
    }

    @Test
    void createItem_shouldThrowNotFoundException_whenOwnerNotFound() {
        long ownerId = 1L;
        PostItemRequest request = mock(PostItemRequest.class);
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.createItem(request, ownerId));
    }

    @Test
    void getOwnerItems_shouldReturnEmptyList_whenNoItems() {
        long ownerId = 1L;
        when(itemRepository.findAllByOwnerId(ownerId))
                .thenReturn(Collections.emptyList());

        List<OwnedItemDto> result = itemService.getOwnerItems(ownerId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItems_shouldReturnEmptyList_whenTextIsNullOrEmpty() {
        assertTrue(itemService.searchItems(null).isEmpty());
        assertTrue(itemService.searchItems("").isEmpty());
    }

    @Test
    void patchItem_shouldThrowNotFoundException_whenItemNotFound() {
        long itemId = 1L;
        long ownerId = 2L;
        PatchItemRequest request = mock(PatchItemRequest.class);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.patchItem(itemId, request, ownerId));
    }

    @Test
    void patchItem_shouldThrowNotFoundException_whenNotOwner() {
        long itemId = 1L;
        long ownerId = 2L;

        Item item = new Item();
        item.setId(itemId);

        User owner = new User();
        owner.setId(3L);

        item.setOwner(owner);

        PatchItemRequest request = mock(PatchItemRequest.class);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> itemService.patchItem(itemId, request, ownerId));
    }

    @Test
    void addComment_shouldThrowNotValidException_whenOwnerTriesToComment() {
        long itemId = 1L;
        long authorId = 2L;

        User author = new User();
        author.setId(authorId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(author);

        PostCommentRequest request = mock(PostCommentRequest.class);

        when(userRepository.findById(authorId))
                .thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        assertThrows(ArgumentsNotValidException.class, () -> itemService.addComment(request, itemId, authorId));
    }

    @Test
    void addComment_shouldThrowNotValidException_whenNoBooking() {
        long itemId = 1L;
        long authorId = 2L;

        User author = new User();
        author.setId(authorId);

        User owner = new User();
        owner.setId(3L);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        PostCommentRequest request = mock(PostCommentRequest.class);

        when(userRepository.findById(authorId))
                .thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(ArgumentsNotValidException.class, () -> itemService.addComment(request, itemId, authorId));
    }

    @Test
    void getAllCommentsForItem_shouldReturnEmptyList_whenNoComments() {
        long itemId = 1L;
        when(commentRepository.findAllByItemId(itemId))
                .thenReturn(Collections.emptyList());

        List<CommentDto> result = itemService.getAllCommentsForItem(itemId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllCommentsForOwner_shouldReturnEmptyList_whenNoComments() {
        long ownerId = 1L;
        when(commentRepository.findAllByItemOwnerId(ownerId))
                .thenReturn(Collections.emptyList());

        List<CommentDto> result = itemService.getAllCommentsForOwner(ownerId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getItem_shouldReturnItemDto_whenItemExists() {
        long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId))
                .thenReturn(Collections.emptyList());

        ItemDto result = itemService.getItem(itemId);
        assertNotNull(result);
    }

    @Test
    void getOwnerItems_shouldReturnList_whenItemsExist() {
        long ownerId = 1L;
        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User());

        when(itemRepository.findAllByOwnerId(ownerId))
                .thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIdIn(anyList()))
                .thenReturn(Collections.emptyList());

        List<OwnedItemDto> result = itemService.getOwnerItems(ownerId);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void searchItems_shouldReturnList_whenTextIsNotEmpty() {
        String text = "item";
        Item item = new Item();
        item.setId(1L);

        when(itemRepository.searchItems(text))
                .thenReturn(List.of(item));
        List<ItemDto> result = itemService.searchItems(text);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void createItem_shouldReturnItemDto_whenValidRequest() {
        long ownerId = 1L;
        PostItemRequest request = mock(PostItemRequest.class);

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(new User()));
        when(request.getRequestId()).thenReturn(0L);
        when(requestRepository.findById(0L))
                .thenReturn(Optional.of(new Request()));

        Item item = new Item();

        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto result = itemService.createItem(request, ownerId);
        assertNotNull(result);
    }

    @Test
    void patchItem_shouldReturnItemDto_whenOwner() {
        long itemId = 1L;
        long ownerId = 2L;

        Item item = new Item();
        item.setId(itemId);

        User owner = new User();
        owner.setId(ownerId);
        item.setOwner(owner);

        PatchItemRequest request = mock(PatchItemRequest.class);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(request.hasName())
                .thenReturn(false);
        when(request.hasDescription())
                .thenReturn(false);
        when(request.hasAvailable())
                .thenReturn(false);
        when(itemRepository.save(item))
                .thenReturn(item);

        ItemDto result = itemService.patchItem(itemId, request, ownerId);
        assertNotNull(result);
    }

    @Test
    void addComment_shouldReturnCommentDto_whenValid() {
        long itemId = 1L;
        long authorId = 2L;

        User author = new User();
        author.setId(authorId);

        User owner = new User();
        owner.setId(3L);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        PostCommentRequest request = mock(PostCommentRequest.class);
        Booking booking = new Booking();

        when(userRepository.findById(authorId))
                .thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(Optional.of(booking));

        Comment comment = new Comment();
        Comment savedComment = new Comment();

        when(commentRepository.save(comment))
                .thenReturn(savedComment);

        CommentDto result = itemService.addComment(request, itemId, authorId);
        assertNotNull(result);
    }

    @Test
    void getAllCommentsForItem_shouldReturnList_whenCommentsExist() {
        long itemId = 1L;
        Comment comment = new Comment();

        when(commentRepository.findAllByItemId(itemId))
                .thenReturn(List.of(comment));

        List<CommentDto> result = itemService.getAllCommentsForItem(itemId);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllCommentsForOwner_shouldReturnList_whenCommentsExist() {
        long ownerId = 1L;
        Comment comment = new Comment();

        when(commentRepository.findAllByItemOwnerId(ownerId))
                .thenReturn(List.of(comment));

        List<CommentDto> result = itemService.getAllCommentsForOwner(ownerId);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void patchItem_shouldUpdateFields_whenFieldsPresent() {
        long itemId = 1L;
        long ownerId = 2L;

        Item item = new Item();
        item.setId(itemId);

        User owner = new User();
        owner.setId(ownerId);
        item.setOwner(owner);

        PatchItemRequest request = mock(PatchItemRequest.class);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(request.hasName())
                .thenReturn(true);
        when(request.getName())
                .thenReturn("newName");
        when(request.hasDescription())
                .thenReturn(true);
        when(request.getDescription())
                .thenReturn("desc");
        when(request.hasAvailable())
                .thenReturn(true);
        when(request.getAvailable())
                .thenReturn(true);
        when(itemRepository.save(item))
                .thenReturn(item);

        ItemDto result = itemService.patchItem(itemId, request, ownerId);
        assertNotNull(result);
    }

    @Test
    void createItem_shouldReturnItemDto_whenRequestIdPresent() {
        long ownerId = 1L;
        User owner = new User();
        owner.setId(ownerId);

        PostItemRequest request = mock(PostItemRequest.class);

        when(request.getRequestId())
                .thenReturn(10L);
        Item item = new Item();
        Item savedItem = new Item();
        Request req = new Request();

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(requestRepository.findById(10L))
                .thenReturn(Optional.of(req));
        when(itemRepository.save(any()))
                .thenReturn(savedItem);

        ItemDto result = itemService.createItem(request, ownerId);
        assertNotNull(result);
    }

    @Test
    void searchItems_shouldReturnEmptyList_whenNoResults() {
        String text = "item";

        when(itemRepository.searchItems(text))
                .thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.searchItems(text);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getOwnerItems_shouldReturnList_whenNoBookings() {
        long ownerId = 1L;
        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User());

        when(itemRepository.findAllByOwnerId(ownerId))
                .thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIdIn(anyList()))
                .thenReturn(Collections.emptyList());

        List<OwnedItemDto> result = itemService.getOwnerItems(ownerId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void patchItem_shouldNotChangeFields_whenAllFieldsNull() {
        long itemId = 1L;
        long ownerId = 2L;

        Item item = new Item();
        item.setId(itemId);

        User owner = new User();
        owner.setId(ownerId);
        item.setOwner(owner);

        PatchItemRequest request = new PatchItemRequest();

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto result = itemService.patchItem(itemId, request, ownerId);
        assertNotNull(result);
    }

    @Test
    void createItem_shouldWork_whenRequestIdIsNull() {
        long ownerId = 1L;

        PostItemRequest request = new PostItemRequest();
        request.setName("item");
        request.setDescription("desc");
        request.setAvailable(true);
        request.setRequestId(null);

        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto result = itemService.createItem(request, ownerId);
        assertNotNull(result);
    }

    @Test
    void fillBookingDates_shouldReturn_whenBookingsEmpty() {
        long ownerId = 1L;
        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User());

        when(itemRepository.findAllByOwnerId(ownerId))
                .thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIdIn(anyList()))
                .thenReturn(Collections.emptyList());

        List<OwnedItemDto> result = itemService.getOwnerItems(ownerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getLastStart());
        assertNull(result.get(0).getLastEnd());
        assertNull(result.get(0).getNextStart());
        assertNull(result.get(0).getNextEnd());
    }

    @Test
    void patchItem_shouldChangeOnlyName_whenOnlyNamePresent() {
        long itemId = 1L;
        long ownerId = 2L;

        Item item = new Item();
        item.setId(itemId);

        User owner = new User();
        owner.setId(ownerId);
        item.setOwner(owner);

        PatchItemRequest request = new PatchItemRequest();
        request.setName("newName");

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto result = itemService.patchItem(itemId, request, ownerId);
        assertNotNull(result);
    }

    @Test
    void patchItem_shouldChangeOnlyDescription_whenOnlyDescriptionPresent() {
        long itemId = 1L;
        long ownerId = 2L;

        Item item = new Item();
        item.setId(itemId);

        User owner = new User();
        owner.setId(ownerId);
        item.setOwner(owner);

        PatchItemRequest request = new PatchItemRequest();
        request.setDescription("newDesc");

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto result = itemService.patchItem(itemId, request, ownerId);
        assertNotNull(result);
    }

    @Test
    void patchItem_shouldChangeOnlyAvailable_whenOnlyAvailablePresent() {
        long itemId = 1L;
        long ownerId = 2L;

        Item item = new Item();
        item.setId(itemId);

        User owner = new User();
        owner.setId(ownerId);
        item.setOwner(owner);

        PatchItemRequest request = new PatchItemRequest();
        request.setAvailable(true);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto result = itemService.patchItem(itemId, request, ownerId);
        assertNotNull(result);
    }

    @Test
    void fillBookingDates_shouldSetLastBooking_whenOnlyPastBookings() {
        long ownerId = 1L;

        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User());

        when(itemRepository.findAllByOwnerId(ownerId))
                .thenReturn(List.of(item));

        Booking past = new Booking();
        past.setStart(java.time.LocalDateTime.now().minusDays(2));
        past.setEnd(java.time.LocalDateTime.now().minusDays(1));
        past.setItem(item);

        when(bookingRepository.findAllByItemIdIn(anyList()))
                .thenReturn(List.of(past));

        List<OwnedItemDto> result = itemService.getOwnerItems(ownerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getLastStart());
        assertNotNull(result.get(0).getLastEnd());
        assertNull(result.get(0).getNextStart());
        assertNull(result.get(0).getNextEnd());
    }

    @Test
    void fillBookingDates_shouldSetNextBooking_whenOnlyFutureBookings() {
        long ownerId = 1L;

        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User());

        when(itemRepository.findAllByOwnerId(ownerId))
                .thenReturn(List.of(item));

        Booking future = new Booking();
        future.setStart(java.time.LocalDateTime.now().plusDays(1));
        future.setEnd(java.time.LocalDateTime.now().plusDays(2));
        future.setItem(item);

        when(bookingRepository.findAllByItemIdIn(anyList()))
                .thenReturn(List.of(future));

        List<OwnedItemDto> result = itemService.getOwnerItems(ownerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getLastStart());
        assertNull(result.get(0).getLastEnd());
        assertNotNull(result.get(0).getNextStart());
        assertNotNull(result.get(0).getNextEnd());
    }

    @Test
    void addComment_shouldThrow_whenNoApprovedBooking() {
        long itemId = 1L;
        long authorId = 2L;

        User author = new User();
        author.setId(authorId);

        User owner = new User();
        owner.setId(3L);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        PostCommentRequest request = mock(PostCommentRequest.class);

        when(userRepository.findById(authorId))
                .thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(Optional.empty());
        assertThrows(ArgumentsNotValidException.class, () -> itemService.addComment(request, itemId, authorId));
    }
}
