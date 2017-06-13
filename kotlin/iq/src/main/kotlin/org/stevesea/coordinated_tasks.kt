package org.stevesea

import java.util.*
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
 */


class MyThread(
        val tId: Int,
        val queue: BlockingQueue<Optional<Int>>) : Runnable {

    override fun run() {
        while (true) {
            val i = queue.take()
            println("t$tId: val: ${i.get()}")
            queue.put(Optional.of(i.get() + 1))

            // brief wait so that this thread doesn't immediately grab the item off the queue
            TimeUnit.MILLISECONDS.sleep(1)
        }
    }
}
fun coordinated_threads(nTasks: Int) {

    val q = ArrayBlockingQueue<Optional<Int>>(1, true)

    val executorService = Executors.newFixedThreadPool(5)

    // submit N tasks, all tasks should be waiting on queue
    for (i in 1..nTasks) {
        val f = executorService.submit(MyThread(i, q))
    }

    // set initial value into queue
    q.put(Optional.of(1))

    TimeUnit.MILLISECONDS.sleep(50)

    println("Shutting down all threads...")
    executorService.shutdownNow()
}


fun main(args: Array<String>) {
    coordinated_threads(3)
}
