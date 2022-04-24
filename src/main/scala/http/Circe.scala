package http

import io.circe.Decoder.Result
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe._
import io.circe.parser.decode
import cats.Traverse


  case class Product(productId: Long, price: Double, countryCurrency: String, inStock: Boolean)

  object Product {
    implicit val decoder: Decoder[Product] = deriveDecoder[Product]
    implicit val encoder: Encoder[Product] = deriveEncoder[Product]

    def main(args: Array[String]): Unit = {
      val inputString: String =
        """
          |{
          |   "productId": 111112222222,
          |   "price": 23.45,
          |   "countryCurrency": "USD",
          |   "inStock": true
          |}
          |""".stripMargin

      decode[Product](inputString) match {
        case Right(productObject) => println(productObject)
        case Left(ex) => println(s"Ooops some errror here ${ex}")
      }
    }
  }
case class Form(firstName: String, lastName: String, age: Int, email: Option[String])
import io.circe.generic.auto._
object Form {
//  implicit val decoder: Decoder[Form] = deriveDecoder[Form]
//  implicit val encoder: Encoder[Form] = deriveEncoder[Form]

  def main(args: Array[String]): Unit = {Form
    val inputString =
      """
        |[
        |    {"firstName": "Rose", "lastName":"Jane", "age":20, "email":"roseJane@gmail.com"},
        |    {"firstName": "John", "lastName":"Doe" , "age": 45}
        |]
        |""".stripMargin

    parser.decode[List[Form]](inputString) match {
      case Right(form) => println(form)
      case Left(ex) => println(s"Ooops something happened ${ex}")
    }
  }
}
case class CountryCode(name:String,code:String)

object CountryCode extends App{
  implicit val decoder:Decoder[CountryCode] = (hCursor: HCursor) =>
    for{
      name <- hCursor.downField("result").downArray.get[String]("name")
      code <- hCursor.downField("result").downArray.get[String]("code")
    } yield CountryCode(name,code)


  def get(url: String): String = scala.io.Source.fromURL(url).mkString
    val url = "https://api.printful.com/countries"
    val c = get(url)
  val inputStrings = c
  import io.circe._, io.circe.parser._
  val parseResult: Either[ParsingFailure, Json] = parse(inputStrings)

//  parser.decode[List[CountryCode]](inputStrings) match {
//    case Right(countryCode) => println(countryCode)
//    case Left(s) => println(s)
//  }
  parseResult match {
    case Left(parsingError) =>
      throw new IllegalArgumentException(s"Invalid JSON object: ${parsingError.message}")
    case Right(json) =>{
      for{
//        name <- json.hcursor.downField("result").downArray.get[String]("name")
//        code <- json.hcursor.downField("result").downArray.get[String]("code")
        name <- json.hcursor.downField("result").downArray.right.downField("name").as[String]
        code <- json.hcursor.downField("result").downArray.right.downField("code").as[String]



      } println(List(CountryCode(name,code)))
    }


  }

}
case class ProductResrsource(name: String, campaignResources: List[Int], discountPrice: List[Int])

object voucher {

  implicit val decoder: Decoder[ProductResrsource] = new Decoder[ProductResrsource] {
    override def apply(hCursor: HCursor): Result[ProductResrsource] =
      for {
        name <- hCursor.downField("name").as[String]
        orderItemsJson <- hCursor.downField("orderItems").as[List[Json]]
        campaignResource <- Traverse[List].traverse(orderItemsJson)(
          itemJson => itemJson.hcursor.downField("voucher").downField("campaignNumber").as[Int]
        )
        discountPrice <- Traverse[List].traverse(orderItemsJson)(orderItemsJson => {
          orderItemsJson.hcursor.downField("voucher").downField("discount").as[Int]
        })
      } yield {
        ProductResrsource(name, campaignResource, discountPrice)
      }
  }

  def main(args: Array[String]): Unit = {
    val inputString =
      """
        |[
        |   {
        |      "name":"productResource",
        |      "orderItems":[
        |         {
        |            "voucher":{
        |               "campaignNumber":12,
        |               "discount":20,
        |               "subscriptionPeriod":"June"
        |            }
        |         },
        |         {
        |            "voucher":{
        |               "campaignNumber":13,
        |               "discount":24
        |            }
        |         }
        |      ]
        |   },
        |   {
        |      "name":"productResource2",
        |      "orderItems":[
        |         {
        |            "voucher":{
        |               "campaignNumber":13,
        |               "discount":24
        |            }
        |         }
        |      ]
        |   },
        |   {
        |      "name":"productResource3",
        |      "orderItems":[
        |         {
        |            "voucher":{
        |               "campaignNumber":15,
        |               "discount":28
        |            }
        |         }
        |      ]
        |   }
        |]
        |""".stripMargin
    parser.decode[List[ProductResrsource]](inputString) match {
      case Right(vouchers) => vouchers.map(println)
      case Left(ex) => println(s"Something wrong ${ex}")
    }
  }
}
