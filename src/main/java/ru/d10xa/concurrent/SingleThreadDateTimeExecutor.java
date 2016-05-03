package ru.d10xa.concurrent;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.time.LocalDateTime.now;

public class SingleThreadDateTimeExecutor implements DateTimeExecutor {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final PriorityBlockingQueue<DateTimeCallable<?>> queue = new PriorityBlockingQueue<>();
    private final int size;
    private final AtomicInteger queueApproximateSize = new AtomicInteger();

    public SingleThreadDateTimeExecutor(int size) {
        this.size = size;
        start();
    }

    private void start() {
        this.executor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (queueApproximateSize.get() < 1 && executor.isShutdown()) {
                    break;
                }
                DateTimeCallable peek = queue.peek();
                if (peek == null) {
                    continue;
                }
                if (actionAvailableNow(peek.getDateTime())) {
                    try {
                        DateTimeCallable<?> take = queue.take();
                        take.getCallable().call();
                        queueApproximateSize.decrementAndGet();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean actionAvailableNow(LocalDateTime peekDateTime) {
        LocalDateTime now = now();
        return now.isEqual(peekDateTime) ||
                now.isAfter(peekDateTime) ||
                this.queueApproximateSize.get() >= this.size;
    }

    @Override
    public void schedule(LocalDateTime dateTime, Callable<Integer> callable) {
        queue.offer(new DateTimeCallable<>(dateTime, callable));
        queueApproximateSize.incrementAndGet();
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executor.awaitTermination(timeout, unit);
    }

}
