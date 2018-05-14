package no.erlyberly.bootlegterraria.helpers;

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

    private ScheduledExecutorService executorService;
    private Set<ScheduledFuture> tasks;

    public CancellableThreadScheduler() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        tasks = Collections.newSetFromMap(new WeakHashMap<>());
    }

    /**
     * Cancel all future and running tasks
     */
    public void cancelTasks() {
        for (ScheduledFuture sf : tasks) {
            sf.cancel(true);
        }
    }

    /**
     * Execute a task as soon as possible
     *
     * @param runnable
     *     What to do
     */
    public void execute(Runnable runnable) {
        tasks.add(executorService.schedule(runnable, 0, TimeUnit.NANOSECONDS));
    }

    /**
     * Run a task in the future
     *
     * @param runnable
     *     What to do
     * @param ms
     *     How many milliseconds to wait before running the task
     */
    public void schedule(Runnable runnable, long ms) {
        tasks.add(executorService.schedule(runnable, ms, TimeUnit.MILLISECONDS));
    }

    /**
     * Shut down the thread
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
