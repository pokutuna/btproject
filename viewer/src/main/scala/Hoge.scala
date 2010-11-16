import org.btproject.db._


import org.btproject.model._
import org.btproject._
import org.btproject.util._
object Hoge extends TimestampUtil{
  val db = new DBGraphSelector(ConfigLoader.loadFile("config.xml"))

  
  def main(args:Array[String]):Unit = {
    val a = db.getLogBetween("2010/11/04 10:20:00", "2010/11/04 10:40:00")
    println(a)
  }
}

