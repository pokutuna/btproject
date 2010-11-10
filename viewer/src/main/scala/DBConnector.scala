package org.btproject.db

import org.btproject._
import org.scalaquery.session._
import org.scalaquery.session.Database._
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.ql._
import org.scalaquery.ql.basic._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.ql.extended._

object DBConnector {
  val driver = "org.h2.Driver"
  val driverType = H2Driver
  def dbPath(cl:ConfigLoader):String = cl.logDir + "/" + cl.dbName
  val dbFileSuffix = ".h2.db"
  def dbFilePath(cl:ConfigLoader):String = dbPath(cl) + dbFileSuffix
  def apply(cl:ConfigLoader):Database = {
    val path = dbPath(cl)
    val db = Database.forURL("jdbc:h2:"+path, driver = driver)
    return db
  }
}










