package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryDataJpaTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager em;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User(null, "owner", "owner@email.com");
        em.persist(owner);
        item = new Item(null, "item", "desc", true, owner, null);
        em.persist(item);
        em.flush();
    }

    @Test
    @DisplayName("findAllByOwnerId возвращает предметы владельца")
    void findAllByOwnerId() {
        List<Item> items = itemRepository.findAllByOwnerId(owner.getId());
        assertThat(items).isNotEmpty().contains(item);
    }

    @Test
    @DisplayName("findById возвращает предмет по id")
    void findById() {
        Item found = itemRepository.findById(item.getId()).orElse(null);
        assertThat(found).isEqualTo(item);
    }

    @Test
    @DisplayName("searchItems возвращает по поиску")
    void searchItems() {
        List<Item> items = itemRepository.searchItems("item");
        assertThat(items).contains(item);
    }
}
