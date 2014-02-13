package dontblock

import java.util.concurrent.{BlockingQueue}

trait Runnable {
  def run(): Unit
}

trait Executor {
  def execute(command: Runnable): Unit
}

class ThreadPoolExecutor extends Executor {
  val pool: Set[Thread] = Set.empty
  val tasks: BlockingQueue[Runnable] = ???

  def execute(command: Runnable): Unit = {
    //run command on new thread, or queue it, or reject it, depending on settings...
  }
}

trait Callable[V] {
  def call(): V
}

trait Future[V] {
  def isDone(): Boolean
  def get(): V
}

trait ExecutorService extends Executor {
  def submit(task: Runnable): Future[_]
  def submit[T](task: Callable[T]): Future[T]
}
