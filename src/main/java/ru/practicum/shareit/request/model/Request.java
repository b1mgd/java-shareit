package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString(exclude = {"requestor", "items"})
@EqualsAndHashCode(exclude = {"requestor", "items"})
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id")
    private User requestor;

    @CreationTimestamp
    private LocalDateTime created;

    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY)
    private Set<Item> items;
}
