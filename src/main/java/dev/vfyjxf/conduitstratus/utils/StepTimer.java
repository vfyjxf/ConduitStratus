package dev.vfyjxf.conduitstratus.utils;


import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

public class StepTimer {
    private static final Logger log = LoggerFactory.getLogger(StepTimer.class);
    private MutableList<Duration> steps = Lists.mutable.withInitialCapacity(10);
    private volatile Instant start = null;

    public void start() {
        start = Instant.now();
    }

    public void stop() {
        if (start == null) {
            throw new IllegalStateException("Timer not started");
        }
        steps.add(Duration.between(start, Instant.now()));
        start = null;
    }

    public void clear() {
        steps.clear();
        start = null;
    }

    public void print(Logger logger, String task) {
        if (start != null) {
            logger.error("Timer still running");
        }
        Duration total = Duration.ZERO;
        for (Duration step : steps) {
            total = total.plus(step);
        }

        logger.info("Running: {} - Total: {} ms in {} steps", task, total.toNanos() / 1000_000.0, steps.size());
    }


}
