package spakjob

import org.apache.spark.sql.{DataFrame, SparkSession}

object SparkFunctions extends App{
  //sparkSession
  def spark: SparkSession = {
    import org.apache.spark.sql.SparkSession
    SparkSession.builder()
      .appName("ne")
      .master("local[*]")
      .getOrCreate()
  }

  //reading from kafka
  def readFromKafka(spark:SparkSession,topic: String) = {
   spark.readStream.format("kafka")
      .option("kafka.bootstrap.servers","localhost:9092")
      .option("subscribe",topic)
      .load()
  }

  //writing df to mongodb which is passed to ForeachBatch
  def saveToMongoDB: (DataFrame, Long) => Unit = (df: DataFrame, batchId :Long) => {
    val mongo_url = "mongodb://localhost:27017/log.val"
    df.write.format("mongo")
     .mode("overwrite")
     .option("uri", mongo_url)
     .save()
  }


  def saveToMySql: (DataFrame, Long) => Unit = (df: DataFrame, batchId: Long) => {
    val url = """jdbc:mysql://localhost:3306/anything"""
    df.write.format("jdbc")
      .option("url", url)
      .option("dbtable", "test")
      .option("user", "root")
      .option("password", "mySQL_PASSWORD")
      .mode("append")
      .save()
        }

  //writing files the spark destination using foreachbatch method
  def writeToSink(df:DataFrame): Unit = {
    df.writeStream
      .outputMode("complete")
      .foreachBatch(saveToMongoDB)
      .start()
  }

}
