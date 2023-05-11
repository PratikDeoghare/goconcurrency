package goconcurrency

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration.Duration

case class Go()(implicit val ec: ExecutionContextExecutor) {
  private var goroutines = Seq[Future[Any]]()

  def go[T](f: => () => Unit): Unit = {
    goroutines = goroutines :+ Future {
      f()
    }
  }

  def await(): Unit = {
    Await.result(Future.sequence(goroutines), Duration.Inf)
    ()
  }
}