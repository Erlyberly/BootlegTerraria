package no.erlyberly.bootlegterraria.util;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Run (cancellable) tasks on another thread
 *
 * @author kheba
 */
public class CancellableThreadScheduler {

    private final ScheduledExecutorService executorService;
    private final Set<ScheduledFuture> tasks;

    public CancellableThreadScheduler() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.tasks = Collections.newSetFromMap(new WeakHashMap<>());
    }

    /**
     * Cancel all future and running tasks
     */
    public void cancelTasks() {
        for (final ScheduledFuture sf : this.tasks) {
            sf.cancel(true);
        }
    }

    public int size() {
        return this.tasks.size();
    }

    /**
     * Execute a task as soon as possible
     *
     * @param runnable
     *     What to do
     */
    public void execute(final Runnable runnable) {
        this.tasks.add(this.executorService.schedule(runnable, 0, TimeUnit.NANOSECONDS));
    }

    /**
     * Run a task in the future
     *
     * @param runnable
     *     What to do
     * @param ms
     *     How many milliseconds to wait before running the task
     */
    public void schedule(final Runnable runnable, final long ms) {
        this.tasks.add(this.executorService.schedule(runnable, ms, TimeUnit.MILLISECONDS));
    }

    /**
     * Shut down the thread
     */
    public void shutdown() {
        this.executorService.shutdown();
    }
}
