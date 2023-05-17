package chapter4test


import akka.actor.{ActorSystem, Props}
import akka.testkit.{CallingThreadDispatcher, EventFilter, TestKit}
import chapter3.{Greeter, Greeting}
import chapter3test.Greeter01Test.testSystem
import chapter4.LifeCycleHooks
import chapter4.LifeCycleHooks.{ForceRestart, SampleMessage}
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike
import testdriven.StopSystemAfterAll

class LifeCycleHooksTest extends TestKit(ActorSystem("LifCycleTest")) with AnyWordSpecLike with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    system.terminate()
  }

  "The Child" must {
    "log lifecycle hooks" in {
      val testActorRef = system.actorOf(
        Props[LifeCycleHooks], "LifeCycleHooks")
      testActorRef ! "restart"
      testActorRef.tell("msg", testActor)
      expectMsg("msg")
      system.stop(testActorRef)
      Thread.sleep(1000)
    }
  }
}