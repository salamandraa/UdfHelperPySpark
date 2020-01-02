import core.LikeZIO
import org.apache.spark.sql.{Dataset, SparkSession}
import org.scalatest._

import scala.collection.mutable
import scala.collection.parallel.immutable


object SimpleUdfWithLikeZioSpec {

  case class Apple(id: Int, colour: String, size: Double)

  case class AppleWareHouse(apples: List[Apple])

  def getApples(implicit spark: SparkSession): Dataset[Apple] = {
    import spark.implicits._

    List(
      Apple(1, "red", 10.0),
      Apple(2, "green", 15.0),
      Apple(3, "grey", 20.0),
      Apple(4, "red", 25.0)
    ).toDS()
  }

  def getAppleWareHouses(implicit spark: SparkSession): Dataset[AppleWareHouse] = {
    import spark.implicits._
    List(
      AppleWareHouse(
        List(
          Apple(1, "red", 10.0),
          Apple(2, "green", 15.0),
          Apple(3, "grey", 20.0))
      ),
      AppleWareHouse(
        List(Apple(4, "red", 25.0))
      )).toDS()
  }

}

class SimpleUdfWithLikeZioSpec extends FlatSpec with Matchers with SparkTest with BeforeAndAfter {

  import SimpleUdfWithLikeZioSpec._
  import spark.implicits._
  import org.apache.spark.sql.functions._

  it should "simple udf with int" in {

    val applesIn = getApples

    val udfAdd2 = udf { value: Int =>

      implicit val likeZioLog: LikeZIO[Int] = LikeZIO(value + 1, "Start")

      val result = for {
        valueAdd1 <- likeZioLog.map(_ => value + 1)

        _ <- LikeZIO.addLog("add 1")
        valueAdd2 <- LikeZIO(valueAdd1 + 1)
        _ <- LikeZIO.addLog("add 1")
        _ <- LikeZIO.addLog("End")
      } yield valueAdd2

      result.prepareForSpark
    }

    applesIn.show()
    val applesWithAdd2 = applesIn.withColumn("add2", udfAdd2(col("id")))

    applesWithAdd2.printSchema()
    applesWithAdd2.show(false)

    val udfHyperbola = udf { value: Int =>

      implicit val likeZioLog: LikeZIO[Int] = LikeZIO(1 / (value - 2), "Start")

      val result = likeZioLog
        .addLog("Calculated")
        .addLog("End")


      result.prepareForSpark
    }

    val applesWithHyperbola = applesIn.withColumn("hyperbola", udfHyperbola(col("id")))

    applesWithHyperbola.printSchema()
    applesWithHyperbola.show(false)


  }

  it should "" in {
    val appleWareHouses = getAppleWareHouses

    val udfCount = udf { value: mutable.Seq[Apple] =>

      implicit val likeZioLog: LikeZIO[Int] = LikeZIO(value.size, "Start")

      val result = likeZioLog
        .addLog("Calculated")
        .addLog("End")


      result.prepareForSpark
    }

    appleWareHouses.printSchema()
    val appleWareHousesWithSize = appleWareHouses.withColumn("count", udfCount(col("apples")))

    appleWareHousesWithSize.printSchema()
    appleWareHousesWithSize.show(false)
  }

  after {
    dropWarehouseLocation()
  }

}
