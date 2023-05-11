package goconcurrency

import java.util.concurrent.locks.{Condition, ReentrantLock}
import scala.annotation.targetName
import scala.collection.mutable

case class Channel[T](cap: Int) {
  var queue: mutable.Queue[T] = mutable.Queue[T]()
  var lock: ReentrantLock = new ReentrantLock()
  var notFull: Condition = lock.newCondition()
  var notEmpty: Condition = lock.newCondition()

  def isEmpty: Boolean = queue.isEmpty

  def isFull: Boolean = queue.length == cap

  def send(x: T): Unit = {
    lock.lock()
    try {
      while (isFull) {
        notFull.await()
      }
      queue = queue.enqueue(x)
      notEmpty.signal()
    } finally {
      lock.unlock()
    }
  }

  infix def `<--`(x: T): Unit = {
    send(x)
  }


  def recv(): T = {
    lock.lock()
    try {
      while (isEmpty) {
        notEmpty.await()
      }
      val r = queue.dequeue()
      notFull.signal()
      r
    } finally {
      lock.unlock()
    }
  }

}

package object goconcurrency {
  def `<--`[T](ch: Channel[T]): T = ch.recv()
}