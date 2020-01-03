package udfs.examples


import core.{UdfHelper, LikeZIO}
import org.apache.spark.sql.api.java.UDF1

import UdfHelper._


class UdfHyperbola extends UDF1[Int, LikeZIO.LikeZIOForSpark[Int]] {
  override def call(t1: Int): LikeZIO.LikeZIOForSpark[Int] = UdfAdd2.javaUdf(t1)
}

object UdfHyperbola extends Udf1Helper[Int, Int] {

  override protected def udfFun: Int => LikeZIO[Int] = {
    value: Int =>

      implicit val likeZioLog: LikeZIO[Int] = LikeZIO(1 / (value - 2), "Start")

      likeZioLog
        .addLog("Calculated")
        .addLog("End")
  }
}
