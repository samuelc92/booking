package booking

import zio.json.{DeriveJsonCodec, JsonCodec}
import sttp.tapir.Schema

package object dtos {

  final case class GetEmployeeResponse(id: Int, name: String)  
  object GetEmployeeResponse:
    implicit val jsonCodec: JsonCodec[GetEmployeeResponse] = DeriveJsonCodec.gen
    implicit val schema: Schema[GetEmployeeResponse] = Schema.derived
}
