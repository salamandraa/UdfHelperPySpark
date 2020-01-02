package core


import scala.util.{Failure, Success, Try}


final class LikeZIO[+A](_dirtyLogger: DirtyLogger, _eitherOpt: Option[Either[Seq[Throwable], Option[A]]]) {

  import LikeZIO._

  def addLog(log: String): LikeZIO[A] = {
    _dirtyLogger.addLog(log)
    this
  }

  def addLog(logs: Seq[String]): LikeZIO[A] = {
    _dirtyLogger.addLog(logs)
    this
  }

  @inline def map[U](f: A => U): LikeZIO[U] = {
    val resultEitherOpt = this._eitherOpt.map {
      case Left(errors) => Left(errors)
      case Right(optValue) => Right(optValue.map(f))
    }
    LikeZIO(dirtyLogger = _dirtyLogger, eitherOpt = resultEitherOpt)
  }

  @inline def flatMap[U](f: A => LikeZIO[U]): LikeZIO[U] = this.map(f).flatten

  @inline def flatten[U](implicit ev: A <:< LikeZIO[U]): LikeZIO[U] = {
    val thisDirtyLogger = this._dirtyLogger
    val thisEitherOpt = this._eitherOpt

    thisEitherOpt match {
      case Some(thisEither) => thisEither match {
        case Left(thisExceptions) => LikeZIO(dirtyLogger = thisDirtyLogger, eitherOpt = Some(Left(thisExceptions)))
        case Right(thisValueOpt) => thisValueOpt match {
          case Some(thisValue) =>
            val nestedInstance: LikeZIO[U] = ev(thisValue)
            val newLogs = thisDirtyLogger.merge(nestedInstance.dirtyLogger)
            LikeZIO(dirtyLogger = newLogs, eitherOpt = nestedInstance.eitherOpt)
          case None => LikeZIO(dirtyLogger = thisDirtyLogger, eitherOpt = Some(Right(None)))
        }
      }
      case None => LikeZIO(dirtyLogger = thisDirtyLogger, eitherOpt = None)
    }
  }

  @inline def filter(f: A => Boolean): LikeZIO[A] = {
    val resultEitherOpt = this._eitherOpt.map {
      case Left(errors) => Left(errors)
      case Right(optValue) => Right(optValue.filter(f))
    }
    LikeZIO(dirtyLogger = this._dirtyLogger, eitherOpt = resultEitherOpt)
  }

  def prepareForSpark[U >: A]: LikeZIOForSpark[U] = {

    val (value, exceptions) = _eitherOpt match {
      case Some(either) => either match {
        case Left(errors) => None -> Some(errors.map(_.toString))
        case Right(optValue) => optValue -> None
      }
      case None => None -> None
    }

    val logs = _dirtyLogger.logs
    _dirtyLogger.close()

    LikeZIOForSpark(value, exceptions, logs)
  }

  private def dirtyLogger: DirtyLogger = _dirtyLogger

  private def eitherOpt: Option[Either[Seq[Throwable], Option[A]]] = _eitherOpt

  @inline private def copy[U >: A](dirtyLogger: DirtyLogger = this._dirtyLogger, eitherOpt: Option[Either[Seq[Throwable], Option[U]]] = this._eitherOpt): LikeZIO[U] = LikeZIO(dirtyLogger, eitherOpt)
}

object LikeZIO {

  import scala.collection.mutable

  final case class LikeZIOForSpark[T](value: Option[T], exceptions: Option[Seq[String]], logs: Option[mutable.Seq[String]])

  def addLog[T](log: String)(implicit likeZio: LikeZIO[T]): LikeZIO[T] = likeZio.addLog(log)

  def addLog[T](logs: Seq[String])(implicit likeZio: LikeZIO[T]): LikeZIO[T] = likeZio.addLog(logs)


  final def fromLog(log: String): LikeZIO[Nothing] = {
    val dirtyLogger = DirtyLogger()
    dirtyLogger.addLog(log)
    LikeZIO(dirtyLogger = dirtyLogger, eitherOpt = None)
  }


  final def fromLog(logs: Seq[String]): LikeZIO[Nothing] = {
    val dirtyLogger = DirtyLogger()
    dirtyLogger.addLog(logs)
    LikeZIO(dirtyLogger = dirtyLogger, eitherOpt = None)
  }


  final def apply[T](value: => T): LikeZIO[T] = apply(Try(Option(value)))

  final def fromOpt[T](value: => Option[T]): LikeZIO[T] = apply(Try(value))

  final def fromTry[T](value: => Try[T]): LikeZIO[T] = apply(value.map(Option(_)))

  final def apply[T](value: Try[Option[T]]): LikeZIO[T] = {
    val either = value match {
      case Success(value) => Right(value)
      case Failure(exception) => Left(List(exception))
    }

    LikeZIO(dirtyLogger = DirtyLogger(), eitherOpt = Some(either))
  }

  final def failed[T](exception: Throwable): LikeZIO[T] = LikeZIO(dirtyLogger = DirtyLogger(), eitherOpt = Some(Left(List(exception))))

  final def failed[T](exceptions: List[Throwable]): LikeZIO[T] = LikeZIO(dirtyLogger = DirtyLogger(), eitherOpt = Some(Left(exceptions)))

  final def successful[T](result: T): LikeZIO[T] = LikeZIO(dirtyLogger = DirtyLogger(), eitherOpt = Some(Right(Option(result))))


  final private def apply[T](dirtyLogger: DirtyLogger, eitherOpt: Option[Either[Seq[Throwable], Option[T]]]): LikeZIO[T] = {
    new LikeZIO[T](dirtyLogger, eitherOpt)
  }

}