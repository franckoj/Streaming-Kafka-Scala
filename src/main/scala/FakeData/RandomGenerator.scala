package FakeData

import scala.util.Random
import java.io._
import java.sql.Date
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
object randomGenerator {

    case class stocksDataPurchaseVolume(name:String,
                                        date:String,
                                        volume:String)


    //generic way
    def getRandomElementGenericWay[A](list: List[A]): A = {
      val random = new Random()
      list(random.nextInt(list.length))
    }

    def getRandomElementNormalWay (aList:List[String]): Unit = {
      val random = new Random()
      val randomElement: String = aList(random.nextInt(givenList.length))
      //    println(randomElement)
    }

    def simple_WriterExample(content:String,path_filename:String): Unit = {
      try {
        val writer = new FileWriter(path_filename)
        writer.write(content)
        writer.close()
        println("Done")
      } catch {
        case e: IOException =>
          e.printStackTrace()
      }
    }
    val formatter =DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-MM-SS")
    def currentTime= {
      val formatter =DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-MM-SS")
      LocalDateTime.now().format(formatter)
    }
    def localDateTime=LocalDateTime.now().toString

    val givenList: List[String] = List("BTC","WRX","DOGE")
    val volumes:List[Int] = List(10,13,5,7,9)
    def content()= s"${getRandomElementGenericWay(givenList)}" +
      s",${localDateTime}," +
      s"${getRandomElementGenericWay(volumes)}"

    def concurrentFileWritter()= {
      while (10>1) {
        Future({
          println("writing Files concurrently")
          simple_WriterExample(content(), s"rawfile/stocks-$currentTime.json")
          Thread.sleep(1000)
          println("Done Writing")
        })
      }
    }
    def main(args: Array[String]): Unit = {
      concurrentFileWritter()
      println(currentTime)
    }

  }
