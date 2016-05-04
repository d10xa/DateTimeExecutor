package ru.d10xa.concurrent;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.time.LocalDateTime.now;

public class SingleThreadDateTimeExecutorTest {

    @Test
    public void order() throws Exception {

        DateTimeExecutor executor = new SingleThreadDateTimeExecutor(500);
        ArrayList<LocalDateTime> list = new ArrayList<>();
        LocalDateTime now = now();

        IntStream.range(0, 500)
                .parallel()
                .forEach(i -> {
                    LocalDateTime randomDateTime = randomLocalDateTime(now);
                    executor.schedule(randomDateTime, () -> {
                        list.add(randomDateTime);
                        return 42;
                    });
                });

        executor.shutdown();

        Assert.assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        Assert.assertEquals(500, list.size());

        for (int i = 0; i < list.size() - 1; i++) {
            LocalDateTime a = list.get(i);
            LocalDateTime b = list.get(i + 1);
            Assert.assertTrue(a.isBefore(b) || a.isEqual(b));
        }
    }

    @Test
    public void queue_overflow_order() throws Exception {

        DateTimeExecutor executor = new SingleThreadDateTimeExecutor(500);
        ArrayList<LocalDateTime> list = new ArrayList<>();
        LocalDateTime now = now();

        executor.schedule(now(), () -> {
            Thread.sleep(1000);
            return 9;
        });

        IntStream.range(0, 1000)
                .parallel()
                .forEach(i -> {
                    LocalDateTime randomDateTime = randomLocalDateTime(now);
                    executor.schedule(randomDateTime, () -> {
                        list.add(randomDateTime);
                        return 42;
                    });
                });

        executor.shutdown();

        Assert.assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        Assert.assertEquals(1000, list.size());

        for (int i = 0; i < list.size() - 1; i++) {
            LocalDateTime a = list.get(i);
            LocalDateTime b = list.get(i + 1);
            Assert.assertTrue(a.isBefore(b) || a.isEqual(b));
        }
    }

    private static LocalDateTime randomLocalDateTime(LocalDateTime now) {
        return now.plus(ThreadLocalRandom.current().nextInt(50, 550) * 10, ChronoUnit.MILLIS);
    }

}
