package com.innerspaces.innerspace.controller.user;
import com.innerspaces.innerspace.services.user.NotificationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@CrossOrigin("*")
@Slf4j
@RestController
@RequestMapping("/notifications")
public class NotificationsController {


    private final NotificationsService notificationsService;

    public NotificationsController(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    @RequestMapping(value = {"/subscribe/{userId}","/subscribe/{userId}/"}, method = RequestMethod.GET)
    public SseEmitter subscribe(@PathVariable Long userId) {
        return notificationsService.subscribe(userId);
    }


}