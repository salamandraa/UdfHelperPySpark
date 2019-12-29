package core


import scala.util.{Failure, Success, Try}


final class LikeZIO[+A](_logs: Seq[String], _eitherOpt: Option[Either[Seq[Throwable], Option[A]]]) {


  @inline def addLog(log: String): LikeZIO[A] = this.copy(logs = log +: this._logs)

  @inline def addLog(logs: Seq[String]): LikeZIO[A] = this.copy(logs = this._logs ++ logs)

  @inline def map[U](f: A => U): LikeZIO[U] = {
    val resultEitherOpt = this._eitherOpt.map {
      case Left(errors) => Left(errors)
      case Right(optValue) => Right(optValue.map(f))
    }
    LikeZIO(logs = this._logs, eitherOpt = resultEitherOpt)
  }

  @inline def flatMap[U](f: A => LikeZIO[U]): LikeZIO[U] = this.map(f).flatten

  @inline def flatten[U](implicit ev: A <:< LikeZIO[U]): LikeZIO[U] = {
    val thisLogs = this._logs
    val thisEitherOpt = this._eitherOpt

    thisEitherOpt match {
      case Some(thisEither) => thisEither match {
        case Left(thisExceptions) => LikeZIO(logs = thisLogs, eitherOpt = Some(Left(thisExceptions)))
        case Right(thisValueOpt) => thisValueOpt match {
          case Some(thisValue) =>
            val nestedInstance: LikeZIO[U] = ev(thisValue)
            val newLogs = thisLogs ++ nestedInstance.logs
            LikeZIO(logs = newLogs, eitherOpt = nestedInstance.eitherOpt)
          case None => LikeZIO(logs = thisLogs, eitherOpt = Some(Right(None)))
        }
      }
      case None => LikeZIO(logs = thisLogs, eitherOpt = None)
    }
  }

  @inline def filter(f: A => Boolean): LikeZIO[A] = {
    val resultEitherOpt = this._eitherOpt.map {
      case Left(errors) => Left(errors)
      case Right(optValue) => Right(optValue.filter(f))
    }
    LikeZIO(logs = this._logs, eitherOpt = resultEitherOpt)
  }

  private def logs: Seq[String] = _logs

  private def eitherOpt: Option[Either[Seq[Throwable], Option[A]]] = _eitherOpt

  @inline private def copy[U >: A](logs: Seq[String] = this._logs, eitherOpt: Option[Either[Seq[Throwable], Option[U]]] = this._eitherOpt): LikeZIO[U] = LikeZIO(logs, eitherOpt)
}

object LikeZIO {

  final def fromLog(log: String): LikeZIO[Nothing] = LikeZIO(logs = List(log), eitherOpt = None)

  final def fromLog(logs: Seq[String]): LikeZIO[Nothing] = LikeZIO(logs = logs, eitherOpt = None)


  final def apply[T](value: => T): LikeZIO[T] = apply(Try(Option(value)))

  final def fromOpt[T](value: => Option[T]): LikeZIO[T] = apply(Try(value))

  final def fromTry[T](value: Try[T]): LikeZIO[T] = apply(value.map(Option(_)))

  final def apply[T](value: Try[Option[T]]): LikeZIO[T] = {
    val either = value match {
      case Success(value) => Right(value)
      case Failure(exception) => Left(List(exception))
    }

    LikeZIO(logs = Nil, eitherOpt = Some(either))
  }

  final def failed[T](exception: Throwable): LikeZIO[T] = LikeZIO(logs = Nil, eitherOpt = Some(Left(List(exception))))

  final def failed[T](exceptions: List[Throwable]): LikeZIO[T] = LikeZIO(logs = Nil, eitherOpt = Some(Left(exceptions)))

  final def successful[T](result: T): LikeZIO[T] = LikeZIO(logs = Nil, eitherOpt = Some(Right(Option(result))))


  final private def apply[T](logs: Seq[String], eitherOpt: Option[Either[Seq[Throwable], Option[T]]]): LikeZIO[T] = {
    new LikeZIO[T](logs, eitherOpt)
  }

}