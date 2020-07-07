package org.odk.collect.async

import java.util.function.Consumer
import java.util.function.Supplier

/**
 * Runs tasks in the foreground and background
 */
interface Scheduler {

    /**
     * Run a task and then repeat in the foreground
     *
     * @param task   the task to be run
     * @param repeatPeriod the period between each run of the task
     * @return object that allows task to be cancelled
     */
    fun repeat(task: Runnable, repeatPeriod: Long): Cancellable

    /**
     * Run a task in the background (off the UI thread)
     *
     * @param task     the task to be run
     * @param callback run on the foreground once the task is complete
     */
    fun <T> runInBackground(task: Supplier<T>, callback: Consumer<T>)


    /**
     * Schedule a task to run in the background repeatedly even if the app isn't running
     */
    fun scheduleInBackground(tag: String, spec: TaskSpec, repeatPeriod: Long)

    /**
     * Returns true if a task scheduled with a tag is currently running
     */
    fun isRunning(tag: String): Boolean
}