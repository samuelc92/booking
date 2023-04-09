package booking

import zio.json.JsonCodec
import zio.json.DeriveJsonCodec
import sttp.tapir.Schema

sealed trait Error
object Error:
  implicit lazy val codec: JsonCodec[Error] = DeriveJsonCodec.gen

  case class NotFound(message: String) extends Error
  object NotFound:
    implicit lazy val codec: JsonCodec[NotFound] = DeriveJsonCodec.gen
    implicit lazy val schema: Schema[NotFound]   = Schema.derived