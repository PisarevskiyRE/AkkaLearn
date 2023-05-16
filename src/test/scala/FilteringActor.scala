import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit}
import chapter3.{FilteringActor, SendingActor, SilentActor}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec._
import testdriven.StopSystemAfterAll

import scala.util.Random

class FilteringActorTest extends TestKit(ActorSystem("testsystem"))
  with Matchers
  with AnyWordSpecLike
  with StopSystemAfterAll {
  "Фильтрующий актор" must {

    "отфильтровывать определенные сообщения" in {
      import chapter3.FilteringActor._
      val props = FilteringActor.props(testActor, 5)
      val filter = system.actorOf(props, "filter-1")
      filter ! Event(1)
      filter ! Event(2)
      filter ! Event(1)
      filter ! Event(3)
      filter ! Event(1)
      filter ! Event(4)
      filter ! Event(5)
      filter ! Event(5)
      filter ! Event(6)
      val eventIds = receiveWhile() {
        case Event(id) if id <= 5 => id
      }
      eventIds must be(List(1, 2, 3, 4, 5))
      expectMsg(Event(6))
    }


    "отфильтровывать определенные сообщения с помощью expectNoMsg" in {
      import FilteringActor._
      val props = FilteringActor.props(testActor, 5)
      val filter = system.actorOf(props, "filter-2")
      filter ! Event(1)
      filter ! Event(2)
      expectMsg(Event(1))
      expectMsg(Event(2))
      filter ! Event(1)
      expectNoMessage
      filter ! Event(3)
      expectMsg(Event(3))
      filter ! Event(1)
      expectNoMessage
      filter ! Event(4)
      filter ! Event(5)
      filter ! Event(5)
      expectMsg(Event(4))
      expectMsg(Event(5))
      expectNoMessage
    }

  }
}