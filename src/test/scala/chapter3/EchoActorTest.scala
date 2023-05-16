package chapter3

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.scalatest.wordspec.AnyWordSpecLike
import testdriven.StopSystemAfterAll

import scala.concurrent.Await
import scala.language.postfixOps
import scala.util.{Failure, Success}


class EchoActorTest extends TestKit(ActorSystem("testsystem"))
  with AnyWordSpecLike
  with ImplicitSender
  with StopSystemAfterAll {


  "Эхо-актор" must {
    "Ответить тем же сообщением, которое он получает" in {

      import akka.pattern.ask

      import scala.concurrent.duration._
      implicit val timeout = Timeout(3 seconds)
      implicit val ec = system.dispatcher
      val echo = system.actorOf(Props[EchoActor], "echo1")
      val future = echo.ask("some message")
      future.onComplete {
        case Failure(_)   => //handle failure
        case Success(msg) => //handle success
      }

      Await.ready(future, timeout.duration)
    }

    "Ответить тем же сообщением, которое он получил без запроса" in {
      val echo = system.actorOf(Props[EchoActor], "echo2")
      echo ! "some message"
      expectMsg("some message")

    }

  }
}


class EchoActor extends Actor {
  def receive = {
    case msg =>
      sender() ! msg
  }
}

