package org.btproject.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.sql.Timestamp

object TimestampUtil {
  val MINUTE = 60000
  val HOUR = MINUTE * 60
  val DAY = HOUR * 24

  val sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

  val standardTimestamp = TimestampUtil.parse("2000/01/01 00:00:00")
  
  def parse(str:String):Timestamp = {
    val d = sdf.parse(str)
    new Timestamp(d.getTime)
  }

  def format(ts:Timestamp):String = {
    sdf.format(new Date(ts.getTime))
  }

  def diffSecond(ts1:Timestamp, ts2:Timestamp):Int = { 
    ((ts1.getTime - ts2.getTime) / 1000l).toInt
  }


  def cutOff(ts:Timestamp, spanMinutes:Int = 5):Timestamp = {
    val longTime = ts.getTime
    new Timestamp(longTime - (longTime % (MINUTE * spanMinutes)))
  }

  def minutesLater(ts:Timestamp, minutes:Int):Timestamp = {
    new Timestamp(ts.getTime + MINUTE * minutes)
  }

  def minutesBefore(ts:Timestamp, minutes:Int):Timestamp = {
    new Timestamp(ts.getTime - MINUTE * minutes)
  }
}

object TimestampUtilImplicits {
  implicit def stringToTimestamp(s:String): Timestamp =
    TimestampUtil.parse(s)

  implicit def timestampToString(t:Timestamp): String = 
    TimestampUtil.format(t)
}
