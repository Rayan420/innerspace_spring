package com.innerspaces.innerspace.repositories.user;

import com.innerspaces.innerspace.entities.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Long> {
    // find all by owner id
    List<Notifications> findAllByOwnerId(Long ownerId);

    List<Notifications> findTop10ByOwnerIdOrderByCreatedAtDesc(Long ownerId);



}
