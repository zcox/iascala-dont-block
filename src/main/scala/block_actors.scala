package dontblock.actors

import akka.actor._
import akka.routing._

object BlockingActor {
  case class Block(delay: Long)
}

import BlockingActor._

class BlockingActor extends Actor {
  def receive = {
    case Block(delay) => 
      println(s"blocking-${self.path.name} will be blocking ${Thread.currentThread.getName} for $delay msec...")
      Thread.sleep(delay) //simulate a blocking operation
      println(s"blocking-${self.path.name} is done")
  }
}

class OtherActor extends Actor {
  def receive = {
    case msg => println(s"other-${self.path.name} received $msg on ${Thread.currentThread.getName}")
  }
}

object HowToBlockActors extends App {
  val taskCount = 100
  val delay = 5000l
  val system = ActorSystem("how-to-block")

  val blocking = system.actorOf(Props[BlockingActor].withRouter(FromConfig()), "blocking")
  val other = system.actorOf(Props[OtherActor].withRouter(FromConfig()), "other")

  (1 to taskCount) foreach { _ => blocking ! Block(delay) }
  (1 to taskCount) foreach { _ => other ! "foo" }
}

object SeparateDispatchers extends App {
  val taskCount = 100
  val delay = 5000l
  val system = ActorSystem("how-to-block")

  val blocking = system.actorOf(Props[BlockingActor]
    .withRouter(FromConfig())
    .withDispatcher("blocking2-dispatcher"), "blocking2")
  val other = system.actorOf(Props[OtherActor].withRouter(FromConfig()), "other")

  (1 to taskCount) foreach { _ => blocking ! Block(delay) }
  (1 to taskCount) foreach { _ => other ! "foo" }
}
