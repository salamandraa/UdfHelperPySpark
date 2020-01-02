import java.io.File

import org.apache.commons.io.FileUtils

import org.apache.spark.sql.SparkSession

trait SparkTest {
  private val nameWarehouseLocation = "spark-warehouse"
  protected val warehouseLocation: String = new File(nameWarehouseLocation).getAbsolutePath

  protected implicit val spark: SparkSession = SparkSession
    .builder()
    .master("local[*]")
    .appName("Spark Hive Example")
    .config("spark.sql.warehouse.dir", warehouseLocation)
    .enableHiveSupport()
    .getOrCreate()

  protected def dropWarehouseLocation(): Unit = {
    List(new File(nameWarehouseLocation), new File("metastore_db")).foreach(FileUtils.deleteDirectory)
  }


}
