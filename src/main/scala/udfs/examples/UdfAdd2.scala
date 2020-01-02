package udfs.examples

import core.{UdfHelper, LikeZIO}
import org.apache.spark.sql.api.java.UDF1

import UdfHelper._

class UdfAdd2 extends UDF1[Int, LikeZIO.LikeZIOForSpark[Int]] {
  override def call(t1: Int): LikeZIO.LikeZIOForSpark[Int] = UdfAdd2.javaUdf(t1)
}

object UdfAdd2 extends Udf1Helper[Int, Int] {

  override protected def udfFun: Int => LikeZIO[Int] = {
    value: Int =>

      implicit val likeZioLog: LikeZIO[Int] = LikeZIO(value + 1, "Start")

      for {
        valueAdd1 <- likeZioLog.map(_ => value + 1)

        _ <- LikeZIO.addLog("add 1")
        valueAdd2 <- LikeZIO(valueAdd1 + 1)
        _ <- LikeZIO.addLog("add 1")
        _ <- LikeZIO.addLog("End")
      } yield valueAdd2
  }
}
