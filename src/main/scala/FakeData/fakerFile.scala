package FileCreator
import faker._

import java.io.{FileWriter, IOException}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source
import scala.util.{Failure, Random, Success, Try}

object fakerFile {

  final case class webLog(ipV4:String,time:String,domain:String,email:String)

  def formatter: DateTimeFormatter =DateTimeFormatter.ofPattern("MM-dd HH:MM")
  def ipv4: String = Faker.default.ipV4Address()
  def time: String = Faker.default.randomOffsetDateTime().format(formatter)
  def domain: String = Faker.default.url()
  def email: String = Faker.default.emailAddress()
  def countryCode:String = Faker.default.countryCode()

  //generic way
  def getRandomElementGenericWay[A](list: List[A]): A = {
    val random = new Random()
    list(random.nextInt(list.length))
  }
  val givenList: List[String] = List("AD","ZM","ZW")
  val threatIpList: List[String] = List("177.92.32.82",
  "221.143.40.35",
  "170.106.202.140",
  "221.143.40.39",
  "212.64.1.106",
  "69.16.231.139")

  val filenamePath = "src/main/data/threat-ip/threat-ip.txt"

  def threatIpFromFile(filename:String):List[String] = {
    Try(Source.fromFile(filename)) match {
      case Success(value) => value.getLines().toList
      case Failure(msg) => Nil
    }
  }

  def randomThreatIp: String = getRandomElementGenericWay(threatIpFromFile(filenamePath))


  def currentTime = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-MM-SS")
    LocalDateTime.now().format(formatter)
  }

  def weblog = s"$randomThreatIp - - [${LocalDateTime.now()}] https://$domain $email #$countryCode"

  def simple_WriterExample(content : => String,path_filename: => String): Unit = {
    while (true){
    try {
      val writer = new FileWriter(path_filename)
      (1 to 1000000).foreach(x=>writer.write(content+"\n"))
      writer.close()
    } catch {
      case e: IOException =>
        e.printStackTrace()
    }
      println(s"[done] writing to log-$currentTime.txt")
//      Thread.sleep(60000)
    }
  }

  def main(args: Array[String]): Unit = {
    simple_WriterExample(weblog,s"src/main/data/rawFiles/log-$currentTime.txt")
  }
}
