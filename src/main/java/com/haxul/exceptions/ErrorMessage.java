package com.haxul.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class ErrorMessage {
    private final String status;
    private final String description;
    private final String cause;
    private final Date time;
}