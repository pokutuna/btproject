package org.btproject.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.sql.Timestamp

object TimestampUtil {
  val sdf = new SimpleDateFormat("yyy/MM/dd kk:mm:ss")
  def parse(str:String):Timestamp = {
    val d = sdf.parse(str)
    new Timestamp(d.getTime)
  }

  def format(ts:Timestamp):String = {
    sdf.format(new Date(ts.getTime))
  }
}

trait TimestampUtil { 
  implicit def stringToTimestamp(s:String): Timestamp =
    TimestampUtil.parse(s)
  
  implicit def timestampToString(t:Timestamp): String = 
    TimestampUtil.format(t)
}
