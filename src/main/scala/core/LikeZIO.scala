package core


import scala.util.{Failure, Success, Try}


final class LikeZIO[+A](logs: Seq[String], eitherOpt: Option[Either[Seq[Throwable], Option[A]]]) {

  def addLog(log: String): LikeZIO[A] = this.copy(logs = log +: this.logs)

  def addLog(logs: Seq[String]): LikeZIO[A] = this.copy(logs = logs ++: this.logs)



  private def copy[U >: A](logs: Seq[String] = this.logs, eitherOpt: Option[Either[Seq[Throwable], Option[U]]] = this.eitherOpt): LikeZIO[U] = new LikeZIO(logs, eitherOpt)
}

object LikeZIO {

  final def fromLog(log: String): LikeZIO[Nothing] = new LikeZIO(logs = List(log), eitherOpt = None)

  final def fromLog(logs: Seq[String]): LikeZIO[Nothing] = new LikeZIO(logs = logs, eitherOpt = None)


  final def apply[T](value: => T): LikeZIO[T] = apply(Try(Option(value)))

  final def apply[T](value: => Option[T]): LikeZIO[T] = apply(Try(value))

  final def apply[T](value: Try[T]): LikeZIO[T] = apply(value.map(Option(_)))


  final def failed[T](exception: Throwable): LikeZIO[T] = new LikeZIO(logs = Nil, eitherOpt = Some(Left(List(exception))))

  final def failed[T](exceptions: List[Throwable]): LikeZIO[T] = new LikeZIO(logs = Nil, eitherOpt = Some(Left(exceptions)))

  final def successful[T](result: T): LikeZIO[T] = new LikeZIO(logs = Nil, eitherOpt = Some(Right(Option(result))))


  final private def apply[T](value: Try[Option[T]]): LikeZIO[T] = {
    val either = value match {
      case Success(value) => Right(value)
      case Failure(exception) => Left(List(exception))
    }

    new LikeZIO(logs = Nil, eitherOpt = Some(either))
  }

}