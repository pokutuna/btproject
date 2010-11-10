package org.btproject.model

import org.btproject.db.DBTables
import org.btproject.util._
import scala.util.matching.Regex
import java.sql.Timestamp
import scala.collection.mutable._

object LogFilePaser {
  def parse(file:FileWrapper) = new LogFilePaser(file)
}

class LogFilePaser(file:FileWrapper) {
  var annoBuf:List[LogAnnotation] = List()
  var lineBuf:List[LogLine] = List()
  
  file.getLines.filter(_ != "").foreach{ line =>
    line.charAt(0)  match {
      case '[' =>
        this.annoBuf = this.annoBuf ::: LogAnnotation(line).toList
      case _ =>
        println(LogLine(line))
        this.lineBuf = this.lineBuf ::: LogLine(line).toList
    }
                                      }

  val annotations = this.annoBuf
  val logLines = this.lineBuf
  val logedBy:String =
    annotations.find(_.annoType == "LOGGER_BDA") match {
      case Some(anon) => anon.content.getOrElse("")
      case _ => ""
    }

  println("loglines"+logLines)
  println("linebuf"+lineBuf)
}



case class LogAnnotation(annoType:String, content:Option[String]) 
object LogAnnotation {
  def apply(line:String): Option[LogAnnotation] = {
    val pattern = new Regex("""^\[(.+)\](.*)$""", "anonType", "content")
    
    pattern.findFirstMatchIn(line) match {
      case Some(m) =>
        (m.group("anonType"), m.group("content")) match {
        case ("", _) => None
        case (a, "") => Some(LogAnnotation(a, None))
        case (a, c) => Some(LogAnnotation(a, Some(c)))
      }
      case None => None
    }
    


  }
}

case class LogLine(time:Timestamp, name:String, addr:String, signal:Option[Int])
object LogLine extends TimestampUtil {
  def apply(line:String): Option[LogLine] = {
    val seq = line.split("\t").toList
    val addrPattern = """^([\d\w][\d\w]:){5}([\d\w][\d\w])$""".r

    if(3 <= seq.length && seq.length <= 4 &&
       addrPattern.findFirstIn(seq(2)) != None){
         val t = strToOptionTimestamp(seq(0))
         val s = if(seq.length == 4) strToOptionInt(seq(3)) else None
         val tup = (t, seq(1), seq(2), s)
         println(tup)
         tup match {
           case (None, _, _, _) => None
           case (t, "n/a", a, s) => Some(LogLine(t.get,"",a,s))
           case (t, n, a, s) => Some(LogLine(t.get,n,a,s))
//           case _ => println(seq); None
         }
     } else None
  }

  private def strToOptionInt(s:String):Option[Int] = { 
    try {
      Some(s.toInt)
    } catch {
      case _ => None
    }
  }
}  


