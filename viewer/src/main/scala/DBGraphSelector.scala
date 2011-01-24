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
import java.sql.Timestamp

object DBGraphSelector {
  lazy val selector = new DBGraphSelector(ConfigLoader.loadFile("config.xml"))
  def getSelector:DBGraphSelector = selector
}

class DBGraphSelector(val config:ConfigLoader) {
  val db:Database = DBConnector(config)
  
  import scala.collection.mutable.Map
  val nameCache = Map[String,String]()
    
  def addrToName(addr:String):String = {
    nameCache.get(addr) match {
      case None =>
        db.withSession{ 
          val q = (for(na <- NamedAddrs.where(_.addr is addr)) yield na.name).firstOption.getOrElse(addr)
          nameCache += (addr -> q)
          q
        }
      case Some(name) => name
    }
  }

  def getBDADetectsBetween(start:Timestamp, end:Timestamp)(implicit logedBy:String = "") = {
    db.withSession{
      if(logedBy == "") {
        BDATimespanDetects.where(_.time between(start,end)).list
      } else {
        BDATimespanDetects.where(l => (l.time between(start,end)) && (l.logedBy is logedBy)).list
      }

    }
  }

  def getWifiDetectsBetween(start:Timestamp, end:Timestamp)(implicit logedBy:String = "") = {
    db.withSession{
      if (logedBy == "") { 
        WifiTimespanDetects.where(_.time between(start,end)).list
      } else {
        WifiTimespanDetects.where(l => (l.time between(start,end)) && (l.logedBy is logedBy)).list
      }
    }
  }
  
  def getBDARecordsBetween(start:Timestamp, end:Timestamp) = {
    db.withSession{
      BDARecords.where(_.time between(start,end)).list
    }
  }

  def getWifiRecordsBetween(start:Timestamp, end:Timestamp) = {
    db.withSession{
      WifiRecords.where(_.time between(start,end)).list
    }
  }


  //TODO
}


