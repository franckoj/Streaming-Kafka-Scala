package spakjob

import FileCreator.fakerFile.{currentTime, simple_WriterExample, weblog}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaWriter extends App{
  //writes file to dir in a different thread
  def createNewFile(): Unit = {
    new Thread(() => {
      simple_WriterExample(weblog,s"src/main/data/rawFiles/log-$currentTime.txt")
    }).start()
  }

  createNewFile()

  val spark = SparkSession.builder()
    .appName("kafkaWriter")
    .master("local[*]")
    .getOrCreate()

  val sc = spark.sparkContext
  val ssc = new StreamingContext(spark.sparkContext, Seconds(1))

  def readFiles(): Unit ={
    val fileStream:DStream[String] = ssc.textFileStream("src/main/data")
    fileStream.print()
    ssc.start()
    ssc.awaitTermination()
  }
  //reading the text as stream
  val directoryToStream = "src/main/data/rawFiles"
  def readStream:DataFrame = spark.readStream.text(directoryToStream)

  //writing the streaming text to kafka-topics
  def writeToKafka(df:DataFrame, topic:String, checkPointPath:String): Unit = {
    val res = df.writeStream.format("kafka")
      .option("kafka.bootstrap.servers","localhost:9092")
      .option("checkPointLocation",checkPointPath)
      .option("topic",topic)
      .start()
      println(s"[Done] files pushed to kafka")
      res.awaitTermination()
  }

  // event bus
  import SparkApp.topic
  val checkPointLocation = "src/main/checkPoint"
  writeToKafka(readStream,topic,checkPointLocation)

}
