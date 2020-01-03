package core

import java.io.Closeable
import java.util.UUID

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

final class DirtyLogger(_logs: ArrayBuffer[String] = ArrayBuffer.empty, var isClose: Boolean = false) extends Equals with Closeable {
  protected val id: UUID = UUID.randomUUID()


  def logs: Option[ArrayBuffer[String]] = if (isClose) None else Some(_logs)

  def addLog(log: String): DirtyLogger = {
    _logs += log
    this
  }

  def addLog(logs: mutable.Seq[String]): DirtyLogger = {
    _logs ++= logs
    this
  }

  def merge(other: DirtyLogger): DirtyLogger = {
    if (this.id == other.id) {
      this
    } else {
      if (this.isClose) {
        other
      } else {
        other.logs match {
          case Some(otherLogs) =>

            this.addLog(otherLogs)
            other.close()
            this
          case None => this
        }
      }

    }
  }

  private def convert[T](sq: Seq[T]): mutable.Seq[T] = mutable.Seq[T](sq: _*)

  def addLog(logs: Seq[String]): DirtyLogger = addLog(convert(logs))


  override def canEqual(a: Any): Boolean = a.isInstanceOf[DirtyLogger]

  override def equals(that: Any): Boolean =
    that match {
      case that: DirtyLogger =>
        that.canEqual(this) && this.hashCode == that.hashCode && this.logs == that.logs && this.isClose == that.isClose
      case _ => false
    }

  override def hashCode: Int = {
    val prime = 31
    val result0 = 1
    val result1 = prime * result0 + _logs.hashCode;
    prime * result1 + isClose.hashCode()
  }

  override def close(): Unit = {
    isClose = true
  }
}

object DirtyLogger {
  def apply(): DirtyLogger = new DirtyLogger()
}
