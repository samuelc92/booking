package io.github.samuelc92.booking.entities

import java.util.UUID
import zio.json.*

case class Employee(name: String)

object Employee:
  given JsonEncoder[Employee] =
    DeriveJsonEncoder.gen[Employee]
  given JsonDecoder[Employee] =
    DeriveJsonDecoder.gen[Employee]
