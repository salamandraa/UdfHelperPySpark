package core


import scala.util.{Failure, Success, Try}


final class LikeZIO[+A](_dirtyLogger: DirtyLogger, _either: Either[Seq[Throwable], Option[A]]) {

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
    val resultEither = this._either match {
      case Left(errors) => Left(errors)
      case Right(optValue) => Right(optValue.map(f))
    }
    LikeZIO(dirtyLogger = _dirtyLogger, either = resultEither)
  }

  @inline def flatMap[U](f: A => LikeZIO[U]): LikeZIO[U] = this.map(f).flatten

  @inline def flatten[U](implicit ev: A <:< LikeZIO[U]): LikeZIO[U] = {
    val thisDirtyLogger = this._dirtyLogger

    this._either match {
      case Left(thisExceptions) => LikeZIO(dirtyLogger = thisDirtyLogger, either = Left(thisExceptions))
      case Right(thisValueOpt) => thisValueOpt match {
        case Some(thisValue) =>
          val nestedInstance: LikeZIO[U] = ev(thisValue)
          val newLogs = thisDirtyLogger.merge(nestedInstance.dirtyLogger)
          LikeZIO(dirtyLogger = newLogs, either = nestedInstance.either)
        case None => LikeZIO(dirtyLogger = thisDirtyLogger, either = Right(None))
      }
    }


  }

  @inline def filter(f: A => Boolean): LikeZIO[A] = {
    val resultEitherOpt = this._either match {
      case Left(errors) => Left(errors)
      case Right(optValue) => Right(optValue.filter(f))
    }
    LikeZIO(dirtyLogger = this._dirtyLogger, either = resultEitherOpt)
  }

  def prepareForSpark[U >: A]: LikeZIOForSpark[U] = {

    val (value, exceptions) = _either match {
      case Left(errors) => None -> Some(errors.map(_.toString))
      case Right(optValue) => optValue -> None
    }

    val logs = _dirtyLogger.logs
    _dirtyLogger.close()

    LikeZIOForSpark(value, exceptions, logs)
  }

  private def dirtyLogger: DirtyLogger = _dirtyLogger

  private def either: Either[Seq[Throwable], Option[A]] = _either

  @inline private def copy[U >: A](dirtyLogger: DirtyLogger = this._dirtyLogger, either: Either[Seq[Throwable], Option[U]] = this._either): LikeZIO[U] = LikeZIO(dirtyLogger, either)
}

object LikeZIO {

  import scala.collection.mutable

  final case class LikeZIOForSpark[T](value: Option[T], exceptions: Option[Seq[String]], logs: Option[mutable.Seq[String]])

  def addLog[T](log: String)(implicit likeZio: LikeZIO[T]): LikeZIO[T] = likeZio.addLog(log)

  def addLog[T](logs: Seq[String])(implicit likeZio: LikeZIO[T]): LikeZIO[T] = likeZio.addLog(logs)


  final def apply[T](value: => T): LikeZIO[T] = apply(Try(Option(value)))

  final def fromOpt[T](value: => Option[T]): LikeZIO[T] = apply(Try(value))

  final def fromTry[T](value: => Try[T]): LikeZIO[T] = apply(value.map(Option(_)))

  final def apply[T](value: Try[Option[T]]): LikeZIO[T] = {
    val either = value match {
      case Success(value) => Right(value)
      case Failure(exception) => Left(List(exception))
    }

    LikeZIO(dirtyLogger = DirtyLogger(), either = either)
  }

  final def apply[T](value: => T, log: String): LikeZIO[T] = apply(Try(Option(value)), List(log))

  final def fromOpt[T](value: => Option[T], log: String): LikeZIO[T] = apply(Try(value), List(log))

  final def fromTry[T](value: => Try[T], log: String): LikeZIO[T] = apply(value.map(Option(_)), List(log))

  final def apply[T](value: => T, logs: Seq[String]): LikeZIO[T] = apply(Try(Option(value)), logs)

  final def fromOpt[T](value: => Option[T], logs: Seq[String]): LikeZIO[T] = apply(Try(value), logs)

  final def fromTry[T](value: => Try[T], logs: Seq[String]): LikeZIO[T] = apply(value.map(Option(_)), logs)

  final def apply[T](value: Try[Option[T]], logs: Seq[String]): LikeZIO[T] = {
    val either = value match {
      case Success(value) => Right(value)
      case Failure(exception) => Left(List(exception))
    }

    LikeZIO(dirtyLogger = DirtyLogger().addLog(logs), either = either)
  }

  final def failed[T](exception: Throwable): LikeZIO[T] = LikeZIO(dirtyLogger = DirtyLogger(), either = Left(List(exception)))

  final def failed[T](exceptions: List[Throwable]): LikeZIO[T] = LikeZIO(dirtyLogger = DirtyLogger(), either = Left(exceptions))

  final def successful[T](result: T): LikeZIO[T] = LikeZIO(dirtyLogger = DirtyLogger(), either = Right(Option(result)))


  final private def apply[T](dirtyLogger: DirtyLogger, either: Either[Seq[Throwable], Option[T]]): LikeZIO[T] = {
    new LikeZIO[T](dirtyLogger, either)
  }

}