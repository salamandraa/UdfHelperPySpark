package core


import scala.util.{Failure, Success, Try}


final class LikeZIO[+A](logs: Seq[String], eitherOpt: Option[Either[Seq[Throwable], Option[A]]]) {

  @inline def addLog(log: String): LikeZIO[A] = this.copy(logs = log +: this.logs)

  @inline def addLog(logs: Seq[String]): LikeZIO[A] = this.copy(logs = logs ++: this.logs)

  @inline def map[U](f: A => U): LikeZIO[U] = {
    val resultEitherOpt = this.eitherOpt.map {
      case Right(optValue) => Right(optValue.map(f))
      case Left(errors) => Left(errors)
    }
    LikeZIO(logs = this.logs, eitherOpt = resultEitherOpt)
  }

  @inline def flatMap[U](f: A => LikeZIO[U]): LikeZIO[U] = this.map(f).flatten

  @inline def flatten[U](implicit ev: A <:< LikeZIO[U]): LikeZIO[U] = {
    val thisLogs = this.logs
    val thisEitherOpt = this.eitherOpt

    thisEitherOpt match {
      case Some(value) => ???
      case None => this.copy[U](eitherOpt = None)
    }
  }

  @inline def filter(f: A => Boolean): LikeZIO[A] = {
    val resultEitherOpt = this.eitherOpt.map {
      case Right(optValue) => Right(optValue.filter(f))
      case Left(errors) => Left(errors)
    }
    LikeZIO(logs = this.logs, eitherOpt = resultEitherOpt)
  }


  @inline private def copy[U >: A](logs: Seq[String] = this.logs, eitherOpt: Option[Either[Seq[Throwable], Option[U]]] = this.eitherOpt): LikeZIO[U] = LikeZIO(logs, eitherOpt)
}

object LikeZIO {

  final def fromLog(log: String): LikeZIO[Nothing] = LikeZIO(logs = List(log), eitherOpt = None)

  final def fromLog(logs: Seq[String]): LikeZIO[Nothing] = LikeZIO(logs = logs, eitherOpt = None)


  final def apply[T](value: => T): LikeZIO[T] = apply(Try(Option(value)))

  final def apply[T](value: => Option[T]): LikeZIO[T] = apply(Try(value))

  final def apply[T](value: Try[T]): LikeZIO[T] = apply(value.map(Option(_)))


  final def failed[T](exception: Throwable): LikeZIO[T] = LikeZIO(logs = Nil, eitherOpt = Some(Left(List(exception))))

  final def failed[T](exceptions: List[Throwable]): LikeZIO[T] = LikeZIO(logs = Nil, eitherOpt = Some(Left(exceptions)))

  final def successful[T](result: T): LikeZIO[T] = LikeZIO(logs = Nil, eitherOpt = Some(Right(Option(result))))

  final private def apply[T](value: Try[Option[T]]): LikeZIO[T] = {
    val either = value match {
      case Success(value) => Right(value)
      case Failure(exception) => Left(List(exception))
    }

    LikeZIO(logs = Nil, eitherOpt = Some(either))
  }

  final private def apply[T](logs: Seq[String], eitherOpt: Option[Either[Seq[Throwable], Option[T]]]): LikeZIO[T] = {
    new LikeZIO[T](logs, eitherOpt)
  }

}