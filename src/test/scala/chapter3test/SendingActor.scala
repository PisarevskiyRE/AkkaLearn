package chapter3test

import akka.actor.ActorSystem
import akka.testkit.TestKit
import chapter3.SendingActor
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import testdriven.StopSystemAfterAll

import chapter3.SendingActor._
import scala.util.Random

class SendingActor extends TestKit(ActorSystem("testSystem"))
  with Matchers
  with AnyWordSpecLike
  with StopSystemAfterAll {


  "Посылающий актер" must {
    "отправлять сообщение другому агенту после завершения обработки" in {

      val props = SendingActor.props(testActor)
      val sendingActor = system.actorOf(props, "sendingActor")

      val size = 1000
      val maxInclusive = 100000

      def randomEvents = (0 until size).map{ _=>
        Event(Random.nextInt(maxInclusive))
      }.toVector

      val unsorted = randomEvents
      val sortEvents = SortEvents(unsorted)
      sendingActor ! sortEvents

      expectMsgPF() {
        case SortedEvents(events) =>
          events.size must be(size)
          unsorted.sortBy(_.id) must be(events)
      }
    }
  }
}
