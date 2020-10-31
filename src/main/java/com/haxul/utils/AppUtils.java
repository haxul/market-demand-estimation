package com.haxul.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppUtils {

    public <T> T handleError(Throwable e) {
      log.error("Error: " + e);
      return null;
    }
}
