package dontblock.threads

import java.util.concurrent._

class BlockingTask(name: String, latch: CountDownLatch, delay: Long = 5000) extends Runnable {
  def run() = {
    println(s"$name will be blocking ${Thread.currentThread.getName} for $delay msec...")
    Thread.sleep(delay) //simulate a blocking operation
    println(s"$name is done")
    latch.countDown()
  }
}

object HowToBlockExecutors extends App {
  val poolSize = 5
  val taskCount = 3 * poolSize
  val latch = new CountDownLatch(taskCount)

  // val executor = Executors.newFixedThreadPool(poolSize)
  val executor = new scala.concurrent.forkjoin.ForkJoinPool(poolSize)

  (1 to taskCount) foreach { i => 
    executor.execute(new BlockingTask(i.toString, latch))
    println(s"Submitted task $i on ${Thread.currentThread.getName}")
  }
  latch.await()

  println("Done")
  executor.shutdown()
}
