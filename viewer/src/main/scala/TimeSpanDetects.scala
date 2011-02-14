package org.btproject.analysis

import org.btproject._
import org.btproject.util._
import org.btproject.db._
import org.btproject.model._
import org.btproject.graph._
import scala.collection._
import java.sql.Timestamp
import org.scalaquery.session._
import org.scalaquery.session.Database._
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.ql._
import org.scalaquery.ql.basic._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.ql.extended._


case class TimeSpanDetect(addrID:Int, detectDevices:Seq[DeviceAddress]) extends AddrIDMapping {
  lazy val btDevices:Seq[DeviceAddress] =
    detectDevices.filter(_.deviceType == DeviceType.Bluetooth)
  lazy val wifiDevices:Seq[DeviceAddress] =
    detectDevices.filter(_.deviceType == DeviceType.WiFi)
  lazy val getUserDevice:UserDevice = UserDevice.addrIDToUserDevice(addrID)

  import org.btproject.graph._
  lazy val getNode:UserNode = {
    UserNode(getUserDevice.deviceName)(detectDevices.size)
  }
}

object TimeSpanDetect extends HasDBSelector{
  def concat(detects:Seq[TimeSpanDetect]):Seq[DeviceAddress] = {
    detects.foldLeft(Set[DeviceAddress]())(_ ++ _.detectDevices).toSeq
  }

  def timeBetween(begin:Timestamp, end:Timestamp):Seq[TimeSpanDetect] = {
    val records = selector.db.withSession {
      DetectRecords.where(_.time between(begin, end)).list
    }
    records.map(_.addrID).distinct.map { id =>
      val devices = records.filter(_.addrID == id).foldLeft("")((a, b) => SerializedDevice.mergeDeviceString(a, b.devString))
      val a = TimeSpanDetect(id, SerializedDevice.parse(devices))
      a
    }
  }
}

object CommunityExtractor extends HasDBSelector {
  val preDevices = UserDevice.deviceAddresses.toSet
  
  def timeBetween(start:Timestamp, end:Timestamp, window:Int):Iterable[CommunityRecord] = {
    val detects = TimeSpanDetect.timeBetween(start, end)
    fromTimeSpanDetects(detects).map( t => CommunityRecord(start, window, t._1, t._2, t._3))
  }

  def fromTimeSpanDetects(detects:Seq[TimeSpanDetect]):Iterable[(Int,String,String)] = { 
    val cliques = CliqueExtractor.extractFromTimeSpanDetects(2, detects)

    val records = cliques.map { c =>
      val filtered = detects.filter(d => c.contains(d.addrID))
      val preCliqueID = selector.devStringToPreCliqueID(SerializedDevice.mkString(c.toSeq)).get
      val btdevs = filtered.map(_.btDevices.map(_.addrID.get).toSet).reduceLeft(_ & _) -- c
      val envBTID = SerializedDevice.mkString(btdevs.toSeq)
      
      val wifidevs = filtered.map(_.wifiDevices.map(_.addrID.get).toSet).reduceLeft(_ & _)
      val envWFID = SerializedDevice.mkString(wifidevs.toSeq)
      
      (preCliqueID, envBTID, envWFID)
    }
    return records
  }

}
  











