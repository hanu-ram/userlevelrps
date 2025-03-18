package com.example.userlevelrps.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class UserController {

    @PostMapping("/send-sms")
    public Mono<String> sendSms() {
        return Mono.just("Success");
    }

}
