package org.btproject.db


object SerializedDevice {
  lazy val selector = newDBSelector.getSelector
  
  def prefix = "("
  def suffix = ")"

  def toIntSeq(devString:String):Seq[Int] = {
    devString.split("[()]").filter(_ != "").map(_.toInt)
  }
  
  def parse(devString:String):Seq[DeviceAddress] = {
    val devices = toIntSeq(devString).map(selector.addrIDToDeviceAddress(_))
    if (devices.forall(_ != None)) devices.map(_.get)
      else throw new RuntimeException("Invalid addrID")
  }

  def formatDeviceAddresses(devices:Seq[DeviceAddress]):SerializedDevice = {
    format(devices.map(_.address))
  }

  def format(devices:Seq[String]):SerializedDevice = {
    val optIDs = devices.map(selector.addressToAddrID(_))
    val ids = if (optIDs forall (_ != None)) optIDs.map(_.get)
      else throw new RuntimeException("AddressID not found")
    SerializedDevice(mkString(ids))
  }

  def mkString(ids:Seq[Int]):String= {
    val sortedIDs = ids.distinct.sortWith(_ < _)
    val str = (for (n <- sortedIDs) yield prefix + n + suffix).mkString
    str
  }

  def mergeDeviceString(str1:String, str2:String):String = {
    mkString(toIntSeq(str1) ++ toIntSeq(str2))
  }
}

case class SerializedDevice(devString:String) {
  lazy val toDeviceAddresses = SerializedDevice.parse(devString)
  lazy val addrIDs = SerializedDevice.toIntSeq(devString)

  def merge(sdev:SerializedDevice):SerializedDevice = {
    SerializedDevice(SerializedDevice.mergeDeviceString(devString, sdev.devString))
  }
}
