package chapter3

import akka.actor.{Actor, ActorRef, Props}


object FilteringActor {
  def props(nextActor: ActorRef, bufferSize: Int) =
    Props(new FilteringActor(nextActor, bufferSize))
  case class Event(id: Long)
}

class FilteringActor(nextActor: ActorRef,
                     bufferSize: Int) extends Actor {
  import FilteringActor._
  var lastMessages = Vector[Event]()
  def receive = {
    case msg: Event =>
      if (!lastMessages.contains(msg)) {
        lastMessages = lastMessages :+ msg
        nextActor ! msg
        if (lastMessages.size > bufferSize) {
          lastMessages = lastMessages.tail
        }
      }
  }
}
