package com.haxul.headhunter.exceptions;

public class HeadHunterWrongResponseException extends RuntimeException {
    public HeadHunterWrongResponseException(Exception e) {
        super(e);
    }

    public HeadHunterWrongResponseException(String message) {
        super(message);
    }
}
