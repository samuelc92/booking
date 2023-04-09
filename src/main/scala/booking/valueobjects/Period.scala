package booking.valueobjects

import java.time.{OffsetTime, ZoneOffset}

final class Period(sTime: String, eTime: String):
  val startTime = getOffSetTime(sTime)
  val endTime = getOffSetTime(eTime)

  private def getOffSetTime(time: String) =
    OffsetTime.of(time.split(":")(0).toInt, time.split(":")(1).toInt, 0, 0, ZoneOffset.UTC)
