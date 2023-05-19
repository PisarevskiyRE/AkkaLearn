package chapter5

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util._

object FailFuture extends App {

  val failFuture = Future {
    throw new Exception("Ошибка")
  }



  failFuture.onComplete{
    case Success(value) => println(value)
    case Failure(e) => println(e)
  }
}
