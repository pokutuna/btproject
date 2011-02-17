import org.btproject.ConfigLoader
import org.btproject.db._
import org.btproject.util._
import org.btproject.model._
import scala.util.matching.Regex
import org.scalaquery.session._
import org.scalaquery.session.Database._
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.ql._
import org.scalaquery.ql.basic._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.ql.extended._
import java.sql.Timestamp

object ForLogDataCounts extends HasDBSelector {

  def countData = {
    val dateSpans = getDateSpans

    val out = FileWrapper("logDataCounts.csv")
    var str = ""
    str += "devices, logTerms, uniqueCliques\n"

    dateSpans.foreach { s =>
      selector.db.withSession {
        val last = TimestampUtil.minutesBefore(s._2, 1)
        val deviceCount = {
          DetectRecords.where(_.time between(s._1, last)).list.map(_.addrID).distinct.size
        }
        val logCount = Query(DetectRecords.where(_.time between(s._1, last)).count).first
        val uniqComCount = { 
          val cl = CommunityRecords.where(_.time between(s._1, last)).list
          cl.map(_.preCliqueID).distinct.size
        }
        println(s + " : " + deviceCount + " devices " + logCount + " logs " + uniqComCount + " unique cliques")
        str += deviceCount + ", " + logCount + ", " + uniqComCount + "\n"
      }
    }
    out.write(str)
  }

  def getDateSpans = {
    val cl = ConfigLoader.loadFile("config.xml")
    val logStart = TimestampUtil.parse(cl.log_start)
    val logEnd = TimestampUtil.parse(cl.log_end)

    val dayMinutes = 60 * 24
    def nextDay(day:Timestamp) = TimestampUtil.minutesLater(day, dayMinutes)
    
    def recSpanList(start:Timestamp, end:Timestamp):List[(Timestamp, Timestamp)] = {
      if (start.getTime() >= end.getTime()) return Nil
      val next = nextDay(start)
      (start, next) :: recSpanList(next, end)
    }

    recSpanList(logStart, logEnd)
  }
  
  def main(args: Array[String]) = {
//    countData
    println(getDateSpans)
  }
}
