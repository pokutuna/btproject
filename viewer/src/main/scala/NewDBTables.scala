package org.btproject.db

import org.scalaquery.session._
import org.scalaquery.session.Database._
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.ql._
import org.scalaquery.ql.basic._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.ql.extended._
import java.sql.Timestamp

object NewDBTables {
  val tableList = List(
    DeviceAddresses, InvertedDeviceAddresses, DeviceNames, DetectRecords
  )
}

import scala.Enumeration
object DeviceType extends Enumeration {
  val Unknown = Value(0, "Unknown")
  val Bluetooth = Value(1, "Bluetooth")
  val WiFi = Value(2, "WiFi")
}
import DeviceType._

case class DeviceAddress(addrID:Option[Int], address:String, deviceType:DeviceType.Value)

object DeviceAddresses extends ExtendedTable[DeviceAddress]("deviceAddresses") {
  def addrID = column[Int]("addrID", O PrimaryKey, O AutoInc)
  def address = column[String]("address", O NotNull)
  def typeID = column[Int]("typeID", O NotNull)
  
  def * = addrID.? ~ address ~ typeID <>
  ({ t => DeviceAddress(t._1, t._2, DeviceType(t._3))},
   { d:DeviceAddress => Some((d.addrID, d.address, d.deviceType.id))}) //ちょっと不安

  def forInsert = address ~ typeID <>
  ({(a, t) => DeviceAddress(None, a, DeviceType(t))},
   {d:DeviceAddress => Some((d.address, d.deviceType.id))})

  def idx = index("idx", addrID ~ address ~ typeID, unique = true)
}

object InvertedDeviceAddresses extends ExtendedTable[(String, Int)]("invertedDeviceAddresses") {
  def addrID = column[Int]("addrID",O NotNull)
  def address = column[String]("address", O PrimaryKey, O NotNull)
  def * = address ~ addrID
}

case class DeviceName(addrID:Int, name:String)

object DeviceNames extends ExtendedTable[DeviceName]("deviceNames") {
  def addrID = column[Int]("addrID", O PrimaryKey)
  def name = column[String]("name")
  def * = addrID ~ name <> (DeviceName, DeviceName.unapply _)
  def forInsert = addrID ~ name <>
    ({ (a, n) => DeviceName(a, n)}, { d:DeviceName => Some(d.addrID, d.name)})
}

case class DetectRecord(addrID:Int, time:Timestamp, devString:String) {
  import org.btproject.db.SerializedDevice
  lazy val toDeviceAddresses:Seq[DeviceAddress] = SerializedDevice.parse(devString)
}

object DetectRecords extends ExtendedTable[DetectRecord]("detectRecord") {
  def addrID = column[Int]("addrID")
  def time = column[Timestamp]("time")
  def devString = column[String]("devString")
  def * = addrID ~ time ~ devString <> (DetectRecord, DetectRecord.unapply _)
  def forInsert = addrID ~ time ~ devString <>
    ({ (a, t, d) => DetectRecord(a, t, d)},
     { d:DetectRecord => Some(d.addrID, d.time, d.devString)})
  def idxdr = index("idxdr", addrID ~ time, unique = true)
}

case class SerializedClique(devString:String)

case class CommunityRecord(time:Timestamp, coreDevices:String, envDevices:String)

