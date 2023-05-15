import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import chapter3.SilentActor
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import testdriven.StopSystemAfterAll




class SilentActor01Test extends TestKit(ActorSystem("testSystem"))
  with Matchers
  with AnyWordSpecLike
  with StopSystemAfterAll {
  "Немой актор" must {
    "изменяет состояние при получении сообщения, однопоточные" in {

      import chapter3.SilentActor._
      val silentActor = TestActorRef[SilentActor]
      silentActor ! SilentMessage("whisper")
      silentActor.underlyingActor.state must(contain("whisper"))

    }
    "изменяют состояние при получении сообщения, многопоточные" in {
      // Первоначально пишется тест, терпящий неудачу
      fail("пока не реализовано")
    }
  }
}
