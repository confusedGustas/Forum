package org.site.forum.domain.topic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.site.forum.domain.User;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @ManyToOne
    private User author;
}
