package org.btproject

import scala.io.Source
import scala.xml._
import scala.xml.parsing._

object ConfigLoader {

  def loadFile(path:String):ConfigLoader = {
    loadString(Source.fromFile(path)("UTF-8").mkString)
  }

  def loadString(str:String):ConfigLoader = {
    val xml = XML.loadString(str)
    val logDir = (xml \ "log_dir").text
    val dbName = (xml \ "db_name").text
    val log_start = (xml \ "log_start").text
    val log_end = (xml \ "log_end").text

    val data = Map('logDir -> logDir,
                 'dbName -> dbName,
                 'log_start -> log_start,
                 'log_end -> log_end)

    
    val users = for(n <- (xml \\ "user")) yield (new User(n))
    
    val listData = Map('users -> users.toList)
    new ConfigLoader(data, users.toList)
  }
}

class ConfigLoader(val data:Map[Symbol, String], val users:List[User]) {
  def logDir = data('logDir)
  def dbName = data('dbName)
  def log_start = data('log_start)
  def log_end = data('log_end)
}

class User(xml:Node) {
  val name = (xml \ "name").text
  val twitterId = (xml \ "name").text
  
  private val device = (xml \\ "device").head
  val deviceName = (device \ "name").text
  val deviceAddress = (device \ "address").text
  val path:Option[String] = {
    (device \ "logdata_path").text match {
      case "" => None
      case path => Some(path)
    }
  }

  private val classes = (xml \ "classes")
  val grade:String = (classes \ "student" \ "@grade").text
  val laboratory:String = (classes \ "laboratory" \ "@name").text
  val comeFrom:String =  (classes \ "transportation" \ "@from").text
  val transportation:List[String] = (classes \ "transportation" \ "@by").text split(",") toList

  val isStudent:Boolean = classes \ "student" nonEmpty
  val isSmoker:Boolean = classes \ "smoker" nonEmpty
  val isDayPerson:Boolean = classes \ "day-person" nonEmpty
  val isNightPerson:Boolean = classes \ "night-person" nonEmpty
  val isProfessor:Boolean = classes \ "professor" nonEmpty

}
