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


trait DBColumns //Columns to Insert
trait AddrPair

trait DBColumnize {
  def toDBColumn:DBColumns
}

trait AddrPairize {
  val addr:String
  val name:String
  def toNamedAddrs:NamedAddr = 
    NamedAddr(None, addr, name)
}

object DBTables { 
  val tableList = List(BDARecords, WifiRecords, NamedAddrs, InvalidRecords, AnnotationRecords)
}

case class BDARecord(id:Option[Int], logedBy:String, time:Timestamp, bda:String, fileMD5:String) extends DBColumns
object BDARecords extends ExtendedTable[BDARecord]("bdarecords"){
  def id = column[Int]("id", O PrimaryKey, O AutoInc)
  def logedBy = column[String]("logedBy")
  def time = column[Timestamp]("time")
  def bda = column[String]("bda")
  def fileMD5 = column[String]("fileMD5")
  def * = id.? ~ logedBy ~ time ~ bda ~ fileMD5 <> (BDARecord, BDARecord.unapply _)
  def forInsert = logedBy ~ time ~ bda ~ fileMD5 <>
  ({ (l,t,b,f) => BDARecord(None,l,t,b,f)}, { r:BDARecord => Some((r.logedBy, r.time, r.bda, r.fileMD5))})
}

case class WifiRecord(id:Option[Int], logedBy:String, time:Timestamp, macaddr:String, signal:Int, fileMD5:String) extends DBColumns
object WifiRecords extends ExtendedTable[WifiRecord]("wifirecords"){
  def id = column[Int]("id", O PrimaryKey, O AutoInc)
  def logedBy = column[String]("logedBy")
  def time = column[Timestamp]("time")
  def macaddr = column[String]("macaddr")
  def signal = column[Int]("signal")
  def fileMD5 = column[String]("fileMD5")
  def * = id.? ~ logedBy ~ time ~ macaddr ~ signal ~ fileMD5 <> (WifiRecord, WifiRecord.unapply _)
  def forInsert = logedBy ~ time ~ macaddr ~ signal ~ fileMD5 <>
  ({ (l,t,m,s,f) => WifiRecord(None,l,t,m,s,f)}, { r:WifiRecord => Some((r.logedBy, r.time, r.macaddr, r.signal, r.fileMD5))})
}

case class NamedAddr(id:Option[Int], addr:String, name:String) extends AddrPair
object NamedAddrs extends ExtendedTable[NamedAddr]("namedaddrs"){
  def id = column[Int]("id", O PrimaryKey, O AutoInc)
  def addr = column[String]("addr", O NotNull)
  def name = column[String]("name")
  def * = id.? ~ addr ~ name <> (NamedAddr, NamedAddr.unapply _)
  def forInsert = addr ~ name <> ({ (a,n) => NamedAddr(None,a,n)}, { n:NamedAddr => Some((n.addr, n.name))})
}

case class AnnotationRecord(id:Option[Int], logedBy:String, annoType:String, content:String, fileMD5:String) extends DBColumns
object AnnotationRecords extends ExtendedTable[AnnotationRecord]("annotationrecords"){
  def id = column[Int]("id", O PrimaryKey, O AutoInc)
  def logedBy = column[String]("logedBy")
  def annoType = column[String]("annoType")
  def content = column[String]("content")
  def fileMD5 = column[String]("fileMD5")
  def * = id.? ~ logedBy ~ annoType ~ content ~ fileMD5 <> (AnnotationRecord, AnnotationRecord.unapply _)
  def forInsert = logedBy ~ annoType ~ content ~ fileMD5 <> ({ (l,a,c,f) => AnnotationRecord(None,l,a,c,f)}, { a:AnnotationRecord => Some(a.logedBy, a.annoType, a.content, a.fileMD5)})
}

case class InvalidRecord(id:Option[Int], logedBy:String, fileName:String, fileMD5Sum:String, rawLine:String) extends DBColumns
object InvalidRecords extends ExtendedTable[InvalidRecord]("invalidrecords"){
  def id = column[Int]("id", O PrimaryKey, O AutoInc)
  def logedBy = column[String]("logedBy")
  def fileName = column[String]("fileName")
  def fileMD5Sum = column[String]("fileMD5Sum")
  def rawLine = column[String]("rawLine")
  def * = id.? ~ logedBy ~ fileName ~ fileMD5Sum ~ rawLine <> (InvalidRecord, InvalidRecord.unapply _)
  def forInsert = logedBy ~ fileName ~ fileMD5Sum ~ rawLine <> ({ (l,n,m,r) => InvalidRecord(None,l,n,m,r) }, { i:InvalidRecord => Some((i.logedBy, i.fileName, i.fileMD5Sum, i.rawLine))})
}









