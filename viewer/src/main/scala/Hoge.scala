import org.btproject.db._

import org.btproject.model._
import org.btproject._
import org.btproject.util._
import org.btproject.db.DBTables
import org.scalaquery.session._
import org.scalaquery.session.Database._
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.ql._
import org.scalaquery.ql.basic._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.ql.extended._
import scala.collection.mutable.Map
import java.sql.Timestamp
import org.btproject.util.TimestampUtil

object Hoge {
  val selector = new DBGraphSelector(ConfigLoader.loadFile("config.xml"))
  val db = DBConnector(ConfigLoader.loadFile("config.xml"))
  
  def createBDATimespanDetect(start:Timestamp):Unit = {
    val end = TimestampUtil.minutesLater(start,15)
    val logs = selector.getBDARecordsBetween(start,end)
    val store = Map[(String,String), Int]()
    logs.foreach{ log =>
      store.get((log.logedBy, log.addr)) match {
        case None => store((log.logedBy, log.addr)) = 1
        case Some(h) => store((log.logedBy, log.addr)) += 1
      }
    }
    db.withSession(
      store.map{ pair => BDATimespanDetect(None, start, pair._1._1, pair._1._2, pair._2)}
      .foreach( detect => BDATimespanDetects.forInsert.insert(detect))
    )
  }

  def createWifiTimespanDetect(start:Timestamp):Unit = {
    val end = TimestampUtil.minutesLater(start,15)
    val logs = selector.getWifiRecordsBetween(start,end)
    val store = Map[(String,String), Int]()
    val signal = Map[(String,String), List[Int]]()
    logs.foreach{ log =>
      store.get((log.logedBy, log.addr)) match {
        case None => store((log.logedBy, log.addr)) = 1
        case Some(h) => store((log.logedBy, log.addr)) += 1
      }
    }
    db.withSession(
      store.map{ pair => WifiTimespanDetect(None, start, pair._1._1, pair._1._2, pair._2)}
      .foreach( detect => WifiTimespanDetects.forInsert.insert(detect))
    )
  }
  
  def iterateTimeSpans():Unit = {
    db.withSession{ 
    val firstTimeBDA = (
      for{ a <- BDARecords
          _ <- Query.orderBy(a.time)} yield a.time).first

    val endTimeBDA = (for{ a <- BDARecords
        _ <- Query.orderBy(a.time desc)} yield a.time).first
    var start = firstTimeBDA
    while(start.before(endTimeBDA)){
      start = TimestampUtil.cutOff(start)
      println("bdaTimespan:"+start)
      createBDATimespanDetect(start)
      start = TimestampUtil.minutesLater(start,15)
    }

    
    }
  }
  
  def main(args:Array[String]):Unit = {
    db.withSession{
      try {
//        BDATimespanDetects.ddl(DBConnector.driverType).drop
        WifiTimespanDetects.ddl(DBConnector.driverType).drop
      } catch {
        case e: Exception => println(e.printStackTrace())
      }
//      BDATimespanDetects.ddl(DBConnector.driverType).create
      WifiTimespanDetects.ddl(DBConnector.driverType).create

    
    val firstTimeWifi = (
      for{ a <- WifiRecords
          _ <- Query.orderBy(a.time)} yield a.time).first
    val endTimeWifi = {
      for{ a <- WifiRecords
        _ <- Query.orderBy(a.time desc)} yield a.time}.first
    
    var start = firstTimeWifi
    while(start.before(endTimeWifi)){
      start = TimestampUtil.cutOff(start)
      println("wifiTimespan:"+start)
      createWifiTimespanDetect(start)
      start = TimestampUtil.minutesLater(start,15)
    }
    }

//    iterateTimeSpans()
  }
}



