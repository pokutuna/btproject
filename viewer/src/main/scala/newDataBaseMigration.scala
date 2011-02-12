package org.btproject.db

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

object NewDBMigration {
  
  def cleanDataBase(cl:ConfigLoader):Unit = {
//    val dbFile = new java.io.File(DBConnector.dbFilePath(cl))
//    if(dbFile.exists) { dbFile.delete(); println("database deleted") }
    
  }


  def makeTables(db:Database):Unit = {
    NewDBTables.tableList.foreach{ t =>
      db withSession {
        t.ddl(DBConnector.driverType) drop;
        t.ddl(DBConnector.driverType).create
      }
    }
  }

  import org.btproject._
  def insertUsers(users:Seq[User]) = {
    val db = newDBSelector.getSelector
    users.foreach(u => db.addDevice(u.deviceAddress, DeviceType.Bluetooth, u.deviceName))
  }

  def start(user:User):Unit = {
    user.path match {
      case Some(path) =>
        println(user.name + "'s " + user.deviceName + "(" + user.deviceAddress + ")")
        parseAndInsert(user, path)
      case None =>
        println(user.name + "'s " + user.deviceName + " has no logdata")
    }
  }

  val bdaFileNamePattern:Regex = """^(bda)?[0-9]{8}\.tsv""".r
  val wifiFileNamePattern:Regex = """wifi.*\.tsv""".r 

  def parseAndInsert(user:User, path:String):Unit = {
    val db = newDBSelector.getSelector
    val userAddrID = db.addressToAddrID(user.deviceAddress)
    //bda
    FileWrapper(path).expand(bdaFileNamePattern).foreach { f =>
      println(f.file.getPath)
      try {
        insertLogLines(userAddrID.get, DeviceType.Bluetooth, getLogLines(f))
      } catch {
        case e => e.printStackTrace
        println("gave up parsing "+f.file.getPath)
      }
    }

    //wifi
    FileWrapper(path).expand(wifiFileNamePattern).foreach { f =>
      println(f.file.getPath)
      try {      
        insertLogLines(userAddrID.get, DeviceType.WiFi, getLogLines(f))
      } catch {
        case e => e.printStackTrace
        println("gave up parsing "+f.file.getPath)
      }
    }
  }

  import org.btproject.model._
  def getLogLines(file:FileWrapper):Iterator[Either[InvalidLog,LogLine]] = {
    val info = LogFileInfo("", "", "")
    file.getLines.filter(_ != "").map(LogLine(_, info))
  }

  import java.sql.Timestamp
  case class SimpleLogLine(time:Timestamp, address:String, name:String)
  def insertLogLines(id:Int, devType:DeviceType.Value, lines:Iterator[Either[InvalidLog,LogLine]]):Unit = {

    val detects = collectDetects(lines)
    val devices = detects.map( d => (d.address, d.name)).distinct
    
    val db = newDBSelector.getSelector
    devices.foreach{ t => db.addDevice(t._1, devType, t._2)}
    detects.map(_.time).distinct.foreach { t =>
      val addresses = detects.filter(_.time == t).map(_.address)
      val sd = SerializedDevice.format(addresses)
      db.addDetectRecord(DetectRecord(id, t, sd.devString))
    }
  }

  def collectDetects(lines:Iterator[Either[InvalidLog,LogLine]]):Seq[SimpleLogLine] = {
    import org.btproject.util.TimestampUtil._
  
    val detects = scala.collection.mutable.ListBuffer[SimpleLogLine]()
    //collect detects
    for ( line <- lines ) line match {
      case Left(e) => //println("Invalid log found: " + e)
      case Right(log) =>
        log match {
          case log:DetectLog =>
            detects += (SimpleLogLine(TimestampUtil.cutOff(log.time,10), log.addr, log.name))
          case anno:LogAnnotation =>
            /*
            anno.annoType match {
              case str if str == "BT_SCAN" =>
                try {
                  val t = TimestampUtil.parse(anno.content.getOrElse(""))
                  detects += (SimpleLogLine(TimestampUtil.cutOff(t,10), "", ""))
                } catch {
                  case e =>
                }
              case str if str == "WIFI_SCAN" =>
              case _ =>
            }
            */
        }
    }
    return detects.toSeq
  }

  def main(args: Array[String]) = {
    val cl = ConfigLoader.loadFile("config.xml")
    val db = DBConnector(cl)    
    val logDir = FileWrapper(cl.logDir)
//    cleanDataBase(cl)
    makeTables(db)
    
    insertUsers(cl.users)
    for (u <- cl.users) start(u)
  }
}










