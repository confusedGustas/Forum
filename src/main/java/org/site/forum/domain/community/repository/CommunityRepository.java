package org.site.forum.domain.community.repository;

import org.site.forum.domain.community.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommunityRepository extends JpaRepository<Community, UUID> {



}
