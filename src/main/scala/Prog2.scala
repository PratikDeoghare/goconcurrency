import goconcurrency.goconcurrency.`<--`
import goconcurrency.{Channel, Go}

import java.util.concurrent.locks.{Condition, ReentrantLock}
import scala.annotation.targetName
import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}


case class Tree[T](data: T, left: Tree[T] = null, right: Tree[T] = null)

def walk[T](t1: Tree[T], ch: Channel[T]): Unit = {
  if (t1 == null) {
    return
  }
  walk(t1.left, ch)
  ch <-- t1.data
  walk(t1.right, ch)
}

object Prog2 {

  def main(args: Array[String]): Unit = {

    implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global
    val ch1 = Channel[Int](1)
    val ch2 = Channel[Int](1)
    val go = Go()

    val t1 = Tree(3, Tree(1, Tree(1), Tree(2)), Tree(8, Tree(5), Tree(13)))
    val t2 = Tree(8, Tree(3, Tree(1, Tree(1), Tree(2)), Tree(5)), Tree(13))
    
    go.go { () =>
      walk(t1, ch1)
    }
    
    go.go { () =>
      walk(t2, ch2)
    }


    var isSame = true
    go.go { () =>
      while (true) {
        val a = <--(ch1)
        val b = <--(ch2)
        println(s"$a, $b")
        if (a != b) {
          isSame = false
        }
      }
    }

    println(s"$isSame")

    go.await()
  }
}