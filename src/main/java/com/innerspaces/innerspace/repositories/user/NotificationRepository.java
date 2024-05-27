package com.innerspaces.innerspace.repositories.user;

import com.innerspaces.innerspace.entities.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Long> {
    // find all by owner id
    List<Notifications> findAllByOwnerId(Long ownerId);

    List<Notifications> findTop10ByOwnerIdOrderByCreatedAtDesc(Long ownerId);



}
