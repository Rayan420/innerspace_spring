package com.innerspaces.innerspace.controller.user;

import com.innerspaces.innerspace.services.user.NotificationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@CrossOrigin("*")
@Slf4j
@RestController
@RequestMapping("/notifications")
public class NotificationsController {

    private final NotificationsService notificationsService;

    public NotificationsController(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    @GetMapping(value={"/subscribe/{userId}"}, produces = "text/event-stream")
    public Flux<ServerSentEvent<?>> subscribe(@PathVariable Long userId) {
        return notificationsService.subscribe(userId);
    }
}
