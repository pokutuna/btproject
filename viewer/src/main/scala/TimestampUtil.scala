package org.btproject.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.sql.Timestamp

trait TimestampUtil { 
  val sdf = new SimpleDateFormat("yyy/MM/dd kk:mm:ss")
  
  implicit def stringToTimestamp(s:String): Timestamp = {
    val d = sdf.parse(s)
    new Timestamp(d.getTime)
  }
  
  implicit def timestampToString(t:Timestamp): String = {
    sdf.format(new Date(t.getTime))
  }
  
  def strToOptionTimestamp(s:String): Option[Timestamp] = {
    try {
      val d = sdf.parse(s)
      Some(new Timestamp(d.getTime))
    } catch {
      case e => None
    }
  }

}
