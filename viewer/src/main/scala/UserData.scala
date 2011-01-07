package org.btproject.analysis

import org.btproject._
import org.btproject.util._
import org.btproject.db._
import org.btproject.model._
import scala.collection._
import java.sql.Timestamp

case class UserData(addr:String, btDetects:Set[String], wifiDetects:Set[String]) {
  lazy val name:String = DBGraphSelector.getSelector.addrToName(addr)
  lazy val detects = btDetects ++ wifiDetects
}

object UserDataBuilder {
  val db = DBGraphSelector.getSelector

  def timeBetween(begin:Timestamp, end:Timestamp):Iterable[UserData] = {
    val bda = db.getBDADetectsBetween(begin, end)
    val wifi = db.getWifiDetectsBetween(begin, end)
    val detector = bda.map(_.logedBy).toSet ++ wifi.map(_.logedBy).toSet
    detector.map{ addr =>
      UserData(addr, 
               (bda.view filter(_.logedBy == addr) map(_.addr) force).toSet,
               (wifi.view filter(_.logedBy == addr) map(_.addr) force).toSet)
    }
  }
}

