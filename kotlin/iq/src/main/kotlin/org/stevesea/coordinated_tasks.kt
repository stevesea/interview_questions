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
        // retrieve item from queue, waiting if necessary
        val item = queue.take()
        println("t$tId: val: $item")
        queue.put(item + 1)
    }
}

fun coordinated_threads(nTasks: Int) {

    // uses 'fair' locking, which'll grant access in FIFO order
    val q = ArrayBlockingQueue<Int>(1, true)

    // make sure we've got enough threads for all tasks to be running simultaneously
    val executorService = Executors.newScheduledThreadPool(nTasks)

    // submit N tasks to be repeatedly run by the exec service
    //
    // initial delay seems hokey... but, it helped to ensure that the execservice actually starts each task
    // in the expected order. otherwise, seemed to be non-determinisitc. Even using initial-delay of zero
    // worked on linux/java7 but not windows/java8
    (1..nTasks).forEach { i ->
        // schedule thread to be run immediately
        executorService.scheduleWithFixedDelay(MyThread(i, q),
                2,15,TimeUnit.MILLISECONDS)
    }

    // set initial value into queue, the first task will finally stop waiting.
    q.put(1)

    TimeUnit.MILLISECONDS.sleep(100)

    println("Shutting down all threads...")
    executorService.shutdownNow()
}


fun main(args: Array<String>) {
    coordinated_threads(3)
}
