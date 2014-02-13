package dontblock.futures

import scala.concurrent._
import scala.concurrent.duration._
import java.util.concurrent.Executors

object PromisesAndFutures extends App {

  def f1: Future[String] = Promise.successful("f1").future

  def f2: Future[String] = {
    val p = Promise[String]()
    val f = p.future
    p.success("f2")
    f
  }

  def f3: Future[String] = {
    val p = Promise[String]
    new Thread(new Runnable() {
      def run() {
        Thread.sleep(3000)
        p.success("f3")
      }
    }).start()
    p.future
  }

  import ExecutionContext.Implicits.global

  def f4: Future[String] = Future {
    "f4"
  }

  for {
    s1 <- f1
    s2 <- f2
    s3 <- f3
    s4 <- f4
  } {
    println(s"$s1, $s2, $s3, $s4")
  }
}

object HowToBlockFutures extends App {
  import ExecutionContext.Implicits.global

  def body(name: String, delay: Long = 5000) = {
    println(s"$name will be blocking ${Thread.currentThread.getName} for $delay msec...")
    Thread.sleep(delay) //simulate a blocking operation
    println(s"$name is done")
    name
  }

  val taskCount = 3 * Runtime.getRuntime.availableProcessors
  val futures = (1 to taskCount) map { i => 
    println(s"Submitting task $i on ${Thread.currentThread.getName}")
    Future(body(i.toString))
  }

  Await.result(Future.sequence(futures), 1 minute)
  println("Done")
}

object SolutionsToBlockedFutures {
  implicit val defaultContext = ExecutionContext.global
  val databaseContext = ExecutionContext.fromExecutor(null)

  Future("default processing")
  Future("database operations")(databaseContext)
}
