package com.haxul.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AppUtils {

    public <T> T logError(Throwable e) {
        log.error("Error: " + e);
        return null;
    }

    public <T> T resolveFutureOrGetNull(CompletableFuture<T> future, int timout) {
        try {
            return future.get(timout, TimeUnit.MICROSECONDS);
        } catch (Exception e) {
            log.error("Error: " + e);
            return null;
        }
    }

    public String getDateKey() {
        LocalDate curDate = LocalDate.now();
        int day = curDate.getDayOfMonth();
        int month = curDate.getMonth().getValue();
        int year = curDate.getYear();
        return day + "-" + month + "-" + year;
    }
}
