package org.site.forum.domain.user.repository;

import org.jetbrains.annotations.NotNull;
import org.site.forum.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsById(@NotNull UUID id);

}
