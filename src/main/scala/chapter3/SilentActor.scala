package chapter3

import akka.actor.{Actor, ActorRef}

class SilentActor extends Actor{
  var internalState = Vector[String]()

  import SilentActor._

  override def receive: Receive = {
    case SilentMessage(data) =>
      internalState = internalState :+ data
  }

  def state = internalState
}

object SilentActor {
  case class SilentMessage(data: String)
  case class GetState(receiver: ActorRef)
}
