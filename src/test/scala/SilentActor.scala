import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit}
import chapter3.{SendingActor, SilentActor}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import testdriven.StopSystemAfterAll

import scala.util.Random


class SilentActorTest extends TestKit(ActorSystem("testsystem"))
  with AnyWordSpecLike
  with Matchers
  with StopSystemAfterAll {

  "A Silent Actor" must {

    "change internal state when it receives a message, single" in {
      import SilentActor._

      val silentActor = TestActorRef[SilentActor]
      silentActor ! SilentMessage("whisper")
      silentActor.underlyingActor.state must (contain("whisper"))
    }

  }
}

