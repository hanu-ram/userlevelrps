package com.example.userlevelrps.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationExceptions {
    public static <T> Mono<T> userIdNotFound() {
        return Mono.error(UserIdNotFoundException::new);
    }
}
