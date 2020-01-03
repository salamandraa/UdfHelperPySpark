package udfs.examples


import core.LikeZIO
import core.UdfHelper._


object UdfHyperbola extends Udf1Helper[Int, Int] {

  override protected def udfFun: Int => LikeZIO[Int] = {
    value: Int =>

      implicit val likeZioLog: LikeZIO[Int] = LikeZIO(1 / (value - 2), "Start")

      likeZioLog
        .addLog("Calculated")
        .addLog("End")
  }
}
