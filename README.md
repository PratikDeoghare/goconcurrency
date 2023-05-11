## Go Concurrency 

Your beloved goroutines and channels now in Scala.

WARNING: This is highly experimental. I am doing it just to see if it can be done.

```scala
import goconcurrency.goconcurrency.`<--`
import goconcurrency.{Channel, Go}

import java.util.concurrent.locks.{Condition, ReentrantLock}
import scala.annotation.targetName
import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}


object Prog1 {

  def main(args: Array[String]): Unit = {

    implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global
    val ch = Channel[Int](3)

    val go = Go()

    (1 to 15).foreach { x =>
      go.go { () =>                   // launching goroutine 
        ch <-- x                      // chan send 
        println(s"sent $x ")
      }
    }

    (1 to 15).foreach { x =>
      go.go { () =>                   // launching goroutine 
        val x = <--(ch)               // chan receive
        println(s"received $x")
      }
    }

    go.await()
  }
}
```