package org.site.forum.domain.topic.entity;

import jakarta.persistence.*;
import org.site.forum.domain.User;

@Entity
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @ManyToOne
    private User author;
}
