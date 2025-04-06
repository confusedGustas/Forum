package org.site.forum.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.site.forum.domain.comment.entity.Comment;
import org.site.forum.domain.topic.entity.Topic;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    private UUID id;
    private String name;

    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    @JsonManagedReference
    @OneToMany(mappedBy = "author")
    private List<Topic> topics;

}
