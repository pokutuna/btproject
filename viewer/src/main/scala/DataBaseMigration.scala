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

object DBMigration extends DBTables {

  val bdaLogFileNamePattern:Regex = """bda[0-9]{8}\.tsv""".r
  val wifiLogFileNamePattern:Regex = """wifi[0-9]{8}\.tsv""".r 

  def cleanDataBase(cl:ConfigLoader):Unit = {
    val dbFile = new java.io.File(DBConnector.dbFilePath(cl))
    if(dbFile.exists) dbFile.delete()
  }

  def makeTables(db:Database): Unit = {
    tableList.foreach( table =>
      db withSession{ table.ddl(DBConnector.driverType).create }
    )
  }

  def main(args:Array[String]) = {
    val cl = ConfigLoader.loadFile("config.xml")
    val logDir = FileWrapper(cl.logDir)
    cleanDataBase(cl)
    val db = DBConnector(cl)
    makeTables(db)

    logDir.expand(bdaLogFileNamePattern).foreach{ file =>
      println(file.file.getPath)
      val log = new LogFilePaser(file)
      if(log.logedBy != ""){
        
        db withSession{
//          println("dbconnection"+db.toString())
          log.logLines.foreach { line =>
            val r = BDARecord(None, log.logedBy, line.time, line.addr)

            BDARecords.forInsert.insertAll(
              BDARecord(None, log.logedBy, line.time, line.addr)
              )
                                
            val n = NamedAddr(line.addr, line.name)
            val q = for(na <- NamedAddrs
                        if na.addr == n.addr
                        if na.name == n.name) yield na
                                
            q.firstOption match {
              case None => NamedAddrs.insert(n)
              case _ =>
            }
            
          }
        }
      }
      db withSession{
        val q = (for(b <- BDARecords) yield b).list
        println(q)
      }
    }

    println("migrate db")
  }
}





