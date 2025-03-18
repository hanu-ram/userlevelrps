package com.example.userlevelrps.exception;

public class UserIdNotFoundException extends RuntimeException {
    public UserIdNotFoundException() {
        super("User Id not found");
    }
}
