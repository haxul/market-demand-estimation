package com.haxul.analytics.dto;

import com.haxul.analytics.Direction;
import lombok.Data;

@Data
public class Difference {

    private short percentage;
    private Direction direction;
    private int daysAgo;

}
