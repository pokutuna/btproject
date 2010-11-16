package org.btproject.model

import org.btproject.ConfigLoader
import org.btproject.db.DBConnector
import org.btproject.db._
import org.btproject.util.TimestampUtil
import org.scalaquery.session._
import org.scalaquery.session.Database._
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.ql._
import org.scalaquery.ql.basic._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.ql.extended._

class DBGraphSelector(val config:ConfigLoader) {
  val db:Database = DBConnector(config)

  def sample:List[BDARecord] = {
    db.withSession{
      val q = for(log <- BDARecords
        if log.time > TimestampUtil.parse("2010/11/04 10:20:00")
        if log.time < TimestampUtil.parse("2010/11/04 10:40:00")) yield log;
      q.list
    }
  }

  def bdaToName(bda:String):Option[String] = {
    db.withSession{
      val q = for(na <- NamedAddrs.where(_.addr is bda)) yield na.name
      q.firstOption
    }
  }

  import java.sql.Timestamp
  def getLogBetween(start:Timestamp, end:Timestamp) = {
    db.withSession{
      BDARecords.where(_.time between(start,end)).list
//      val q = for(log <-BDARecords
///                if log.time < start && log.time < end) yield log
//      println(q.selectStatement)
//      q.list()
    }
  }

  //TODO
}


