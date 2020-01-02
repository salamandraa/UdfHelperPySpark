package core

import java.io.Closeable
import java.util.UUID

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

final class DirtyLogger(_logs: ArrayBuffer[String] = ArrayBuffer.empty, var isClose: Boolean = false) extends Closeable {
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

  override def close(): Unit = {
    isClose = true
  }
}

object DirtyLogger {
  def apply(): DirtyLogger = new DirtyLogger()
}
