package com.rockthejvm
import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure

object FutureLogger {

  import MyExecContext._
  implicit class FutureLoggerXtension[A](future: Future[A]) {
    def logError: Future[A] = {
      future.failed.foreach { ex =>
        println("Future failed with exception: " + ex.getMessage())
        ex.printStackTrace()
      }
      future
    }
    def debug: Future[A] = {
      future.onComplete {
        case Success(res) => println(res)
        case Failure(ex) =>
          println("Future failed with exception: " + ex.getMessage())
      }
      future
    }

  }

}
