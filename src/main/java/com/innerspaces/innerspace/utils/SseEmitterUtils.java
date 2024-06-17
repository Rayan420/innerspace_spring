package com.innerspaces.innerspace.utils;

import com.innerspaces.innerspace.entities.Notifications;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

public class SseEmitterUtils {

    public static SseEmitter fromFlux(Flux<Notifications> flux, Long timeout) {
        SseEmitter emitter = new SseEmitter(timeout);

        flux.doOnNext(notification -> {
                    try {
                        emitter.send(notification);
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                }).doOnComplete(emitter::complete)
                .doOnError(emitter::completeWithError)
                .subscribe();

        return emitter;
    }
}
