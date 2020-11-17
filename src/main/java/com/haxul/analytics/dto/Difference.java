package com.haxul.analytics.dto;

import com.haxul.analytics.Direction;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Difference {

    private int percentage;
    private Direction direction;
    private int daysAgo;

}
