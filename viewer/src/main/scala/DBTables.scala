package org.btproject.db

import org.scalaquery.session._
import org.scalaquery.session.Database._
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.ql._
import org.scalaquery.ql.basic._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.ql.extended._
import java.sql.Timestamp

trait DBTables{

  val tableList = List(BDARecords, WifiRecords, NamedAddrs)

  case class BDARecord(id:Option[Int], logedBy:String, time:Timestamp, bda:String)
  object BDARecords extends ExtendedTable[BDARecord]("bdarecords"){
    def id = column[Int]("id", O PrimaryKey, O AutoInc)
    def logedBy = column[String]("logedBy")
    def time = column[Timestamp]("time")
    def bda = column[String]("bda")
    def * = id.? ~ logedBy ~ time ~ bda <> (BDARecord, BDARecord.unapply _)
    def forInsert = logedBy ~ time ~ bda <>
      ({ (l,t,b) => BDARecord(None,l,t,b)}, { r:BDARecord => Some((r.logedBy, r.time, r.bda))})
  }

  case class WifiRecord(id:Option[Int], logedBy:String, time:Timestamp, macaddr:String, signal:Int)
  object WifiRecords extends ExtendedTable[WifiRecord]("wifirecords"){
    def id = column[Int]("id", O PrimaryKey, O AutoInc)
    def logedBy = column[String]("logedBy")
    def time = column[Timestamp]("time")
    def macaddr = column[String]("macaddr")
    def signal = column[Int]("signal")
    def * = id.? ~ logedBy ~ time ~ macaddr ~ signal <> (WifiRecord, WifiRecord.unapply _)
    def forInsert = logedBy ~ time ~ macaddr ~ signal <>
    ({ (l,t,m,s) => WifiRecord(None,l,t,m,s)}, { r:WifiRecord => Some((r.logedBy, r.time, r.macaddr, r.signal))})
  }

  case class NamedAddr(addr:String, name:String)
  object NamedAddrs extends ExtendedTable[NamedAddr]("namedaddrs"){
    def addr = column[String]("addr", O PrimaryKey)
    def name = column[String]("name")
    def * = addr ~ name <> (NamedAddr, NamedAddr.unapply _)
  }

}
