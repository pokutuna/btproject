package org.btproject.util

import java.sql.Timestamp
import org.btproject.util.TimestampUtil._

//TODO test
class TimeSpanGenerator(var start:Timestamp, val widthMinutes:Int, val incMinutes:Int) {
  var begin = start
  def getSpan():Pair[Timestamp,Timestamp] = {
    val span = (begin, minutesLater(begin, widthMinutes))
    begin = minutesLater(begin, incMinutes)
    return span
  }
}
