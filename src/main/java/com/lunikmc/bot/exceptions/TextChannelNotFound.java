package com.lunikmc.bot.exceptions;

public class TextChannelNotFound extends RuntimeException {
    public TextChannelNotFound(String message) {
        super(message);
    }
}
