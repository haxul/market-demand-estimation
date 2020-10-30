package com.haxul.exchangeCurrency.exceptions;

public class ExchangeHttpException extends  RuntimeException {
    public ExchangeHttpException(Exception e) {
        super(e);
    }
}
