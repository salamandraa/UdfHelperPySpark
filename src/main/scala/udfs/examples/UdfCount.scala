package udfs.examples


import core.LikeZIO
import core.UdfHelper._

import scala.collection.mutable

case class Apple(id: Int, colour: String, size: Double)


object UdfCount extends Udf1Helper[mutable.Seq[Apple], Int] {


  override protected def udfFun: mutable.Seq[Apple] => LikeZIO[Int] = {

    value: mutable.Seq[Apple] =>


      implicit val likeZioLog: LikeZIO[Int] = LikeZIO(value.size, "Start")

      likeZioLog
        .addLog("Calculated")
        .addLog("End")
  }

}
