package com.haxul.headhunter.models.experience;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum Experience {
    NO_EXPERIENCE("noExperience", "Нет опыта"),
    BETWEEN_1_AND_3("between1And3", "От 1 года до 3 лет"),
    BETWEEN_3_AND_6("between3And6", "От 3 до 6 лет"),
    MORE_THAN_6("moreThan6", "Более 6 лет");

    @Getter
    private final String id;
    @Getter
    private final String name;

    Experience(String id, String name) {
        this.id = id;
        this.name = name;
    }


    @JsonValue
    public String toJson() {
        return id;
    }

    @JsonCreator
    public static Experience fromValue(String value) {
        if (value.equals(NO_EXPERIENCE.getId())) return NO_EXPERIENCE;
        if (value.equals(BETWEEN_1_AND_3.getId())) return BETWEEN_1_AND_3;
        if (value.equals(BETWEEN_3_AND_6.getId())) return BETWEEN_3_AND_6;
        if (value.equals(MORE_THAN_6.getId())) return MORE_THAN_6;
        return null;
    }
}
