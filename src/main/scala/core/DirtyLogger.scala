package core

import java.io.Closeable

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

final class DirtyLogger(_logs: ArrayBuffer[String] = ArrayBuffer.empty, var isClose: Boolean = false) extends Closeable {
  def logs: Option[ArrayBuffer[String]] = if (isClose) None else Some(_logs)

  def addLog(log: String): Unit = _logs += log

  def addLog(logs: mutable.Seq[String]): Unit = _logs ++= logs

  def merge(other: DirtyLogger): DirtyLogger = {
    other.logs match {
      case Some(otherLogs) =>
        this.addLog(otherLogs)
        other.close()
        this
      case None => this
    }

  }

  private def convert[T](sq: Seq[T]): mutable.Seq[T] = mutable.Seq[T](sq: _*)

  def addLog(logs: Seq[String]): Unit = addLog(convert(logs))

  override def close(): Unit = {
    isClose = true
  }
}

object DirtyLogger {
  def apply(): DirtyLogger = new DirtyLogger()
}
