package com.example.invitedate.repository;

import com.example.invitedate.entity.Invitation;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Invitation i set i.viewCount = i.viewCount + 1 where i.id = :id")
    int incrementViewCount(@Param("id") UUID id);
}
