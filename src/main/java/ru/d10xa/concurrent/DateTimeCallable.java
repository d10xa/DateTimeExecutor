package ru.d10xa.concurrent;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public class DateTimeCallable<T> implements Comparable<DateTimeCallable> {

    private final LocalDateTime dateTime;
    private final Callable<T> callable;

    public DateTimeCallable(LocalDateTime dateTime, Callable<T> callable) {
        this.dateTime = dateTime;
        this.callable = callable;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Callable<T> getCallable() {
        return callable;
    }

    @Override
    public int compareTo(DateTimeCallable o) {
        return this.getDateTime().compareTo(o.getDateTime());
    }

    @Override
    public String toString() {
        return this.dateTime.toString();
    }
}
