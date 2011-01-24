package org.btproject.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.sql.Timestamp

object TimestampUtil {
  val MINUTE = 60000
  val sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
  def parse(str:String):Timestamp = {
    val d = sdf.parse(str)
    new Timestamp(d.getTime)
  }

  def format(ts:Timestamp):String = {
    sdf.format(new Date(ts.getTime))
  }

  def cutOff(ts:Timestamp, spanMinutes:Int = 5):Timestamp = {
    val longTime = ts.getTime
    new Timestamp(longTime - (longTime % (MINUTE * spanMinutes)))
  }

  def minutesLater(ts:Timestamp, minutes:Int):Timestamp = {
    new Timestamp(ts.getTime + MINUTE * minutes)
  }
}

object TimestampUtilImplicits {
  implicit def stringToTimestamp(s:String): Timestamp =
    TimestampUtil.parse(s)

  implicit def timestampToString(t:Timestamp): String = 
    TimestampUtil.format(t)
}
