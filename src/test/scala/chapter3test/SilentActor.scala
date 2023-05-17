package chapter3test

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import chapter3.SilentActor
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import testdriven.StopSystemAfterAll


class SilentActorTest extends TestKit(ActorSystem("testsystem"))
  with AnyWordSpecLike
  with Matchers
  with StopSystemAfterAll {

  "A Silent Actor" must {

    "change internal state when it receives a message, single" in {
      import chapter3.SilentActor._

      val silentActor = TestActorRef[SilentActor]
      silentActor ! SilentMessage("whisper")
      silentActor.underlyingActor.state must (contain("whisper"))
    }

  }
}

