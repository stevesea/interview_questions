package org.stevesea

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * problem statement. want to spawn three tasks, want output to look like:
 *
 *    t1: 1
 *    t2: 2
 *    t3: 3
 *    t1: 4
 *    t2: 5
 *    t3: 6
 *    ...
 *
 *  clarifications:
 *      - tasks should always output in that order   (t1, t2, t3)
 *      - tasks assigned IDs at construction
 *      - tasks finished by cntrl-C or whatever (don't need to send shutdown signal)
 */


class MyThread(
        val tId: Int,
        val queue: BlockingQueue<Int>) : Runnable {
    override fun run() {
        // retrieve item from queue, waiting if necessary.
        // queue has been configured for 'fair' access, so first thread to start waiting will
        // be first to be given access.
        val item = queue.take()
        println("t$tId: val: $item")
        //increment the count and put it back onto the queue
        queue.put(item + 1)
    }
}

fun coordinated_threads(nTasks: Int) {

    // uses 'fair' locking, which'll grant access to the queue in FIFO order
    val q = ArrayBlockingQueue<Int>(1, true)

    val executorService = Executors.newScheduledThreadPool(nTasks)

    // submit N tasks to be repeatedly run by the executor
    (1..nTasks).forEach { i ->
        // schedule each thread to be run. after each task runs, delay 1ms.
        executorService.scheduleWithFixedDelay(
                MyThread(i, q),
                0, 1, TimeUnit.MILLISECONDS)

        // this delay seems a bit hokey... but, it helped to ensure that the execservice actually starts each task
        // in the expected order. otherwise, seemed to be non-determinisitc (linux/java7 seemed better behaved
        // than windows/java8)
        TimeUnit.MILLISECONDS.sleep(1L)
    }

    // put an initial value into queue
    q.put(1)

    // wait on the main thread, allow the tasks to do their thing.
    TimeUnit.MILLISECONDS.sleep(20)

    println("Shutting down all threads...")
    executorService.shutdownNow()
}


fun main(args: Array<String>) {
    coordinated_threads(3)
}
