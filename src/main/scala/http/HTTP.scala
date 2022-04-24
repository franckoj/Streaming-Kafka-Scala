package http

import scala.util.{Failure, Success, Try}
import io.circe.parser._
import io.circe.generic.auto._
import org.apache.spark.sql.{DataFrame, SparkSession}

object HTTP extends App {
  //case classes to extract the json fields
  case class CountyData(name: String, code: String)
  case class Data(result: List[CountyData])

  //returns df from the json api using circe
  def HTTPJsonToDF(spark:SparkSession): DataFrame = {
    import spark.implicits._
    def get(url: String): String = {
      val data = scala.io.Source.fromURL(url)
      data.mkString
    }
    val url = "https://api.printful.com/countries"
    val getContent = Try(get(url)) match {
    case Success(value) =>
      val data = decode[Data](value)
      data match {
        case Right(value) => value.result.toDF()
        case Left(s) => s.asInstanceOf
      }
    case Failure(exception) => exception.asInstanceOf
  }
  getContent
}

  val spark = SparkSession.builder()
    .appName("test")
    .master("local[*]")
    .config("spark.sql.shuffle.partitions", 2)
    .getOrCreate()
  HTTPJsonToDF(spark).show()
}
