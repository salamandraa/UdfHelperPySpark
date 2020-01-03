import core.LikeZIO
import org.apache.spark.sql.{Dataset, SparkSession}
import org.scalatest._
import udfs.examples.{UdfAdd2, UdfCount, UdfHyperbola}

import scala.collection.mutable


object SimpleUdfWithLikeZioSpec {

  case class Apple(id: Int, colour: String, size: Double)

  case class AppleWarehouse(apples: List[Apple])

  case class AppleCompany(warehouses: List[AppleWarehouse])

  def getApples(implicit spark: SparkSession): Dataset[Apple] = {
    import spark.implicits._

    List(
      Apple(1, "red", 10.0),
      Apple(2, "green", 15.0),
      Apple(3, "grey", 20.0),
      Apple(4, "red", 25.0)
    ).toDS()
  }

  def getAppleWareHouses(implicit spark: SparkSession): Dataset[AppleWarehouse] = {
    import spark.implicits._
    List(
      AppleWarehouse(
        List(
          Apple(1, "red", 10.0),
          Apple(2, "green", 15.0),
          Apple(3, "grey", 20.0))
      ),
      AppleWarehouse(
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

    val udfAdd2 = UdfAdd2.scalaUdf

    applesIn.show()
    val applesWithAdd2 = applesIn.withColumn("add2", udfAdd2(col("id")))

    applesWithAdd2.printSchema()
    applesWithAdd2.show(false)


    val javaUdfAdd2 = new UdfAdd2().call _
    spark.udf.register("javaUdfAdd2", javaUdfAdd2)

    applesIn.write.saveAsTable("apples")

    val applesWithJavaAdd2 = spark.sql("select id, colour, size, javaUdfAdd2(id) from apples ")

    applesWithJavaAdd2.printSchema()
    applesWithJavaAdd2.show(false)

    val udfHyperbola = UdfHyperbola.scalaUdf

    val applesWithHyperbola = applesIn.withColumn("hyperbola", udfHyperbola(col("id")))

    applesWithHyperbola.printSchema()
    applesWithHyperbola.show(false)


  }

  it should "" in {
    val appleWareHouses = getAppleWareHouses

    val udfCount = UdfCount.scalaUdf

    appleWareHouses.printSchema()
    val appleWareHousesWithSize = appleWareHouses.withColumn("count", udfCount(col("apples")))

    appleWareHousesWithSize.printSchema()
    appleWareHousesWithSize.show(false)
  }

  after {
    dropWarehouseLocation()
  }

}
