package udfs.examples


import core.{LikeZIO, UdfHelper}
import org.apache.spark.sql.api.java.UDF1
import UdfHelper._

import scala.collection.mutable

case class Apple(id: Int, colour: String, size: Double)

class UdfCount extends UDF1[mutable.Seq[Apple], LikeZIO.LikeZIOForSpark[Int]] {

  override def call(t1: mutable.Seq[Apple]): LikeZIO.LikeZIOForSpark[Int] = UdfCount.javaUdf(t1)
}

object UdfCount extends Udf1Helper[mutable.Seq[Apple], Int] {


  override protected def udfFun: mutable.Seq[Apple] => LikeZIO[Int] = {

    value: mutable.Seq[Apple] =>


      implicit val likeZioLog: LikeZIO[Int] = LikeZIO(value.size, "Start")

      likeZioLog
        .addLog("Calculated")
        .addLog("End")
  }

}
