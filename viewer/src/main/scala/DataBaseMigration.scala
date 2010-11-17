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

object DBMigration {

  val bothLogFileNamePattern:Regex = """(bda|wifi)[0-9]{8}\.tsv""".r
  val bdaLogFileNamePattern:Regex = """bda[0-9]{8}\.tsv""".r
  val wifiLogFileNamePattern:Regex = """wifi[0-9]{8}\.tsv""".r 

  def cleanDataBase(cl:ConfigLoader):Unit = {
    val dbFile = new java.io.File(DBConnector.dbFilePath(cl))
    if(dbFile.exists) dbFile.delete()
  }

  def makeTables(db:Database):Unit = {
    DBTables.tableList.foreach( table =>
      db withSession{ table.ddl(DBConnector.driverType).create }
    )
  }

  def insertNamedAddr(na:NamedAddr, db:Database):Unit = {
    if(na.name != ""){ 
      db.withSession{
        val a = for(a <- NamedAddrs
                    if a.name is na.name
                    if a.addr is na.addr) yield a.*
        a.firstOption match {
          case None => NamedAddrs.forInsert.insert(na)
          case _ => 
        }
      }
    }
  }


  def parseAndInsertFile(db:Database, file:FileWrapper):Unit = {
    println("inserting :"+file.file.getPath)
    try{ 
      db withSession{
        (new LogFilePaser(file)).logLines.foreach( _ match { 
          case Left(e) =>
            println("invalid log found: "+e)
            InvalidRecords.insert(e.toDBColumn)
          case Right(log) =>
            log match {
              case bda:BDADetectLog =>
                BDARecords.forInsert.insert(bda.toDBColumn)
              insertNamedAddr(bda.toNamedAddrs, db)
              case wifi:WifiDetectLog =>
                WifiRecords.forInsert.insert(wifi.toDBColumn)
              insertNamedAddr(wifi.toNamedAddrs, db)
              case anno:LogAnnotation =>
                AnnotationRecords.forInsert.insert(anno.toDBColumn)
            }
        })
      }
    } catch {
      case e => println(e.printStackTrace())
      println("gave up parsing "+file.getPath)
    } 
  }

  def insertTimespanDetects(db:Database):Unit = {

  }
  
  def main(args:Array[String]) = {
    val cl = ConfigLoader.loadFile("config.xml")
    val logDir = FileWrapper(cl.logDir)
    cleanDataBase(cl)
    val db = DBConnector(cl)
    makeTables(db)

    logDir.expand(bothLogFileNamePattern).foreach{ file =>
      parseAndInsertFile(db,file)
    }


    println("migrate db")
  }

}





