package org.btproject.model

import org.btproject.db._
import org.btproject.util._
import scala.util.matching.Regex
import java.sql.Timestamp
import scala.collection.mutable._
import scala.util.control.Exception._
import scala.io.Source

object LogFilePaser {
  def parse(file:FileWrapper) = new LogFilePaser(file)
}

class LogFilePaser(file:FileWrapper) {
  val addrPattern = """([\d\w]{2}:[\d\w]{2}:[\d\w]{2}:[\d\w]{2}:[\d\w]{2}:[\d\w]{2})""".r
  val unknownBDA = "00:00:00:00:00:00"
  
  private def searchLogedBy(file:FileWrapper):Option[String] = {
    val logedByPattern =
      """^\[LOGGER_BDA\]([\d\w]{2}:[\d\w]{2}:[\d\w]{2}:[\d\w]{2}:[\d\w]{2}:[\d\w]{2})$""".r
    file.getLines.collect{ case logedByPattern(by) => by}.toTraversable.headOption
  }
  private def getBDAFromPath(file:FileWrapper):Option[String] = {
    file.getPath match {
      case addrPattern(addr) => Some(addr)
      case _ => None
    }
  }
  private def getAnotherLogFileSameDay:Option[FileWrapper] = {
    file.file.getName match {
      case n if n.startsWith("bda") =>
        val name = "bda".r.replaceFirstIn(n, "wifi");
        allCatch.opt(FileWrapper(file.file.getParent + "/" + name))
      case n if n.startsWith("wifi") =>
        val name = "wifi".r.replaceFirstIn(n, "bda")
        allCatch.opt(FileWrapper(file.file.getParent + "/" + name))
      case _ => None
    }  
  }

  val logedBy:String = allCatch.opt(searchLogedBy(file).getOrElse(getBDAFromPath(file).getOrElse(
    getAnotherLogFileSameDay match {
      case Some(path) => searchLogedBy(path).getOrElse(unknownBDA)
      case None => unknownBDA}))).getOrElse(unknownBDA)
  if(logedBy == unknownBDA) println("set unknown BDA")
  
  val info:LogFileInfo = LogFileInfo(logedBy, file.file.getName, file.md5sum)

  val logLines = file.getLines.filter(_ != "").map(LogLine(_,info))
}




/*
import scala.Enumeration
object AnnoType extends Enumeration {
  type AnnoType = Value
  val BT_SCAN, WIFI_SCAN, LOGGER_VERSION, LOGGER_BDA = Value
}
import AnnoType._
*/
case class LogFileInfo(logedBy:String, fileNameFrom:String, fileMD5Sum:String)


trait LogLine
object LogLine {
  def apply(rawLine:String, info:LogFileInfo):Either[InvalidLog, LogLine] = {
    allCatch.opt(rawLine.charAt(0)) match {
      case Some('[') => LogAnnotation(rawLine, info)
      case _ => DetectLog(rawLine, info)
    }
  }
}

case class LogAnnotation(annoType:String, content:Option[String], info:LogFileInfo) extends LogLine with DBColumnize {
  def toDBColumn:AnnotationRecord =
    AnnotationRecord(None, info.logedBy, annoType, content.getOrElse(""), info.fileMD5Sum)
}
object LogAnnotation extends LogLine {
  def apply(rawLine:String, info:LogFileInfo):Either[InvalidLog, LogAnnotation] = {
    val pattern = """^\[(.+)\]([^\[\]]*)$""".r
    rawLine match {
      case pattern(a, "") => Right(LogAnnotation(a, None, info))
      case pattern(a, c) => Right(LogAnnotation(a, Some(c), info))
      case _ => Left(InvalidLog(rawLine, info))
    }
  }
}

trait DetectLog extends LogLine
object DetectLog {
  val addrPattern = """([\d\w]{2}:[\d\w]{2}:[\d\w]{2}:[\d\w]{2}:[\d\w]{2}:[\d\w][\d\w])""".r
  def apply(rawLine:String, info:LogFileInfo):Either[InvalidLog,DetectLog] = {
    val seq = rawLine.split("\t")
    if(3 != seq.length && seq.length != 4)
      return Left(InvalidLog(rawLine, info))
    
    val time:Option[Timestamp] = allCatch.opt(TimestampUtil.parse(seq(0)))
    val name:String = seq(1)
    val addr:Option[String] = addrPattern.findFirstIn(seq(2))
    val signal:Option[Int] = allCatch.opt(seq(3).toInt)
    
    (time,name,addr,signal) match {
      case (None,_,_,_) => Left(InvalidLog(rawLine, info))
      case (_,_,None,_) => Left(InvalidLog(rawLine, info))
      case (Some(t),"n/a",Some(a),None) => Right(BDADetectLog(t,"",a,info))
      case (Some(t),n,Some(a),None) => Right(BDADetectLog(t,n,a,info))
      case (Some(t),n,Some(a),Some(s)) => Right(WifiDetectLog(t,n,a,s,info))
    }
  }
}
  
case class BDADetectLog(time:Timestamp, name:String, addr:String, info:LogFileInfo) extends DetectLog with DBColumnize with AddrPairize{
  def toDBColumn:BDARecord = 
    BDARecord(None, info.logedBy, time, addr, info.fileMD5Sum)
}

case class WifiDetectLog(time:Timestamp, name:String, addr:String, signal:Int, info:LogFileInfo) extends DetectLog with DBColumnize with AddrPairize {
  def toDBColumn:WifiRecord = 
    WifiRecord(None, info.logedBy, time, addr, signal, info.fileMD5Sum)
}

case class InvalidLog(rawline:String, info:LogFileInfo) extends LogLine with DBColumnize {
  def toDBColumn:InvalidRecord =
    InvalidRecord(None, info.logedBy, info.fileNameFrom, info.fileMD5Sum, rawline)
}

