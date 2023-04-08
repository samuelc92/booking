package io.github.samuelc92.booking

import zio.json.{DeriveJsonCodec, JsonCodec}
import sttp.tapir.Schema

package object dtos {
    final case class CreateEmployeeRequest(name: String)  
    object CreateEmployeeRequest:
      implicit val jsonCodec: JsonCodec[CreateEmployeeRequest] = DeriveJsonCodec.gen
      implicit val schema: Schema[CreateEmployeeRequest] = Schema.derived

    final case class GetEmployeeResponse(id: Int, name: String)  
    object GetEmployeeResponse:
      implicit val jsonCodec: JsonCodec[GetEmployeeResponse] = DeriveJsonCodec.gen
      implicit val schema: Schema[GetEmployeeResponse] = Schema.derived
}
