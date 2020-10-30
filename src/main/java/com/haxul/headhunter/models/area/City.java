package com.haxul.headhunter.models.area;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum City {

    SAMARA (78, "Samara");

    @Getter
    private final int id;
    @Getter
    private final String name;
}
