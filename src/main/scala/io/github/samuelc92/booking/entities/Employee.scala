package io.github.samuelc92.booking.entities

import java.util.UUID
import zio.json.{DeriveJsonCodec, JsonCodec}
import sttp.tapir.Schema

case class Employee(id: Int, name: String)

object Employee:
  implicit val jsonCodec: JsonCodec[Employee] = DeriveJsonCodec.gen

  implicit val schema: Schema[Employee] = Schema.derived
