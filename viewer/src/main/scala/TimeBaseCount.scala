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
import org.btproject._
import java.sql.Timestamp

object TimeBaseCount extends HasDBSelector {
  
  val ticks = (0 to ((24 * 60) / 10 - 1) toList)
    
  def makeTable:Unit = {
    selector.db.withSession {
      try {
        CommunityInDayTimes.ddl(DBConnector.driverType).drop
      } catch { case e => println("timeBaseCounts table doesn't exist")}
      CommunityInDayTimes.ddl(DBConnector.driverType).create
    }
  }

  def updateCounts(tick:Int, communities:Seq[Int]) = {
    val pos = TimestampUtil.minutesLater(TimestampUtil.standardTimestamp, tick * 10)
    selector.db.withSession {
      communities.foreach { com =>
        val q = for (c <- CommunityInDayTimes if (c.inDayTime is pos) && (c.cliqueID is com)) yield c.count
        q.firstOption match {
          case None =>
            val record = CommunityInDayTime(pos, com, 1)
            CommunityInDayTimes.forInsert.insert(record)
          case Some(c) => q.update(c + 1)
        }
      }
    }
  }
  
  def calc = {
    val base = TimestampUtil.standardTimestamp    
    val spans = getDateSpans.foreach { span =>
      println(span)
      selector.db.withSession {
        val inDayCommunity = CommunityRecords.where(_.time between(span._1, span._2)).list
        ticks.map { tick =>
          val pos = TimestampUtil.minutesLater(span._1, tick * 10)
          val coms = inDayCommunity.filter(0 <= _.time.compareTo(pos)).filter(_.time.compareTo(TimestampUtil.minutesLater(pos, 10 - 1)) < 0).map(_.preCliqueID)
          updateCounts(tick, coms)
        }
      }
    }

  }
  
  def getDateSpans = {
    ForLogDataCounts.getDateSpans
  }
  
  def main(args: Array[String]) = {
//    makeTable
//    calc
    //todo something
  }
}
