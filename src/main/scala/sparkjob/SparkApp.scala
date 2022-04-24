package spakjob

import http.HTTP.HTTPJsonToDF
import org.apache.spark.sql.{Column, DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, count, expr, regexp_extract, regexp_replace, to_timestamp, window}
import org.apache.spark.sql.streaming.Trigger
import SparkFunctions._
import spakjob.KafkaWriter.{checkPointLocation, readStream, writeToKafka}
import spakjob.Patterns._

import scala.concurrent.Future

object SparkApp {

  def ThreadIpAggregator(): Unit ={

    val res: DataFrame = df.withColumn("Ipv4", regexp_extract(col("value"), patternIp, 0))
      .withColumn("Time", regexp_extract(col("value"), patternTime, 0))
      .withColumn("Email", regexp_extract(col("value"), patternEmail, 0))
      .withColumn("Time", to_timestamp(col("Time")))
      .withColumn("Url", regexp_extract(col("value"), patternUrl, 0))
      .withColumn("CountryCode", regexp_extract(col("value"), patternCountryCode, 0))
      .withColumn("CountryCode", regexp_replace(col("CountryCode"), "#", "")
        .alias("CountryCode")).drop("value")

    // reading static threatIps
    val threatIpPath: String = "src/main/data/threat-ip/threat-ip.txt"
    val ThreadDf = spark.read.text(threatIpPath)
      .withColumnRenamed("value","threat_ip")
    ThreadDf.printSchema()

    //df from the http api response using circe
    val CountryDf: DataFrame = HTTPJsonToDF(spark)
    CountryDf.printSchema()

    //streaming join with static file
    val joinConditionCountryCode: Column = res("CountryCode") === CountryDf("code")
    val dfWithCountryCode: DataFrame = res.join(CountryDf , joinConditionCountryCode).drop("code")

    val joinConditionIpv4: Column = dfWithCountryCode("Ipv4") === ThreadDf("threat_ip")
    val valid_threat_ip: DataFrame = dfWithCountryCode.join(ThreadDf, joinConditionIpv4).drop("ip")
    valid_threat_ip.printSchema()

  //windows aggregation of threat
  val output_df = valid_threat_ip.withWatermark("Time", "10 minutes")
    .groupBy(col("name"), window(col("Time"), "  1 minutes"))
    .agg(count("Ipv4").cast("int").alias("ipv4"))
    .select("*")
    output_df.printSchema()

      //writing to mongodb
    writeToSink(output_df)

      //writing to console
    output_df.writeStream.format("console")
        .outputMode("complete")
        .trigger(Trigger.ProcessingTime("2 second"))
        .start()
        .awaitTermination()

  }

  val spark: SparkSession = SparkSession.builder()
    .appName("kafka_reader")
    .master("local[*]")
    .config("spark.sql.shuffle.partitions",2)
    .getOrCreate()

  val topic = "pwc-scala"

  //initially data is read from kafka streams
  val df: DataFrame = readFromKafka(spark, topic)
    .select(col("topic"), expr("cast(value as string) as value"))

  import scala.concurrent.ExecutionContext.Implicits.global

  def main(args: Array[String]): Unit = {
//    Future(writeToKafka(readStream,topic,checkPointLocation))
    ThreadIpAggregator()
  }
}
