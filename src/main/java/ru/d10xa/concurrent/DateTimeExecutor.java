package ru.d10xa.concurrent;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public interface DateTimeExecutor {

    void schedule(LocalDateTime dateTime, Callable<Integer> callable);

    void shutdown();

    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

}
