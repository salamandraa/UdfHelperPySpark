package udfs.examples

import core.LikeZIO
import core.UdfHelper._


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
