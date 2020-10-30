package com.haxul.headhunter.models.currency;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Currency {
    RUR, USD;

    @JsonValue
    public String toJson() {
        return name();
    }

    @JsonCreator
    public static Currency fromValue(String value) {
        if ("RUR".equals(value)) return RUR;
        if ("USD".equals(value)) return USD;
        return null;
    }

}
