package no.erlyberly.bootlegterraria.util;

import com.badlogic.gdx.Gdx;

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
        executorService = Executors.newSingleThreadScheduledExecutor();
        tasks = Collections.newSetFromMap(new WeakHashMap<>());
    }

    /**
     * Cancel all future and running tasks
     */
    public void cancelTasks() {
        for (final ScheduledFuture sf : tasks) {
            sf.cancel(true);
        }
    }

    public int size() {
        return tasks.size();
    }

    /**
     * Execute a task as soon as possible
     *
     * @param runnable
     *     What to do
     */
    public void executeAsync(final Runnable runnable) {
        tasks.add(executorService.schedule(runnable, 0, TimeUnit.NANOSECONDS));
    }

    /**
     * Execute a task as soon as possible
     *
     * @param runnable
     *     What to do
     */
    public void executeSync(final Runnable runnable) {
        tasks.add(executorService.schedule(() -> Gdx.app.postRunnable(runnable), 0, TimeUnit.NANOSECONDS));
    }

    /**
     * Run a task in the future async
     *
     * @param runnable
     *     What to do
     * @param ms
     *     How many milliseconds to wait before running the task
     */
    public void scheduleAsync(final Runnable runnable, final long ms) {
        tasks.add(executorService.schedule(runnable, ms, TimeUnit.MILLISECONDS));
    }


    /**
     * Run a task in the future on the main thread
     *
     * @param runnable
     *     What to do
     * @param ms
     *     How many milliseconds to wait before running the task
     */
    public void scheduleSync(final Runnable runnable, final long ms) {
        tasks.add(executorService.schedule(() -> Gdx.app.postRunnable(runnable), ms, TimeUnit.MILLISECONDS));
    }

    /**
     * Shut down the thread
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
