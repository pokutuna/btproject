package org.btproject

import scala.io.Source

object ConfigLoader{
  import scala.xml.XML
  import scala.xml.parsing._

  def loadFile(path:String):ConfigLoader = {
    loadString(Source.fromFile(path).mkString)
  }

  def loadString(str:String):ConfigLoader = {
    val xml = XML.loadString(str)
    val logDir = (xml \ "log_dir").text
    val dbName = (xml \ "db_name").text
    val users = for(n <- (xml \ "users" \ "name")) yield n.text
    
    val data = Map('logDir -> logDir,
                 'dbName -> dbName)
    val listData = Map('users -> users.toList)
    new ConfigLoader(data, listData)
  }
}

class ConfigLoader(data:Map[Symbol, String], listData:Map[Symbol, List[String]]){
  def logDir:String = data('logDir)
  def dbName:String = data('dbName)

  def users:List[String] = listData('users)
}
