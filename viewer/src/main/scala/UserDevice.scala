package org.btproject.model

import org.btproject._
import org.btproject.db._
import org.btproject.graph._

class UserDevice(val user:User) extends HasDBSelector {
  def userName:String = user.name
  def address:String = user.deviceAddress
  val addrID:Int = selector.addressToAddrID(address).get
  lazy val deviceName:String = selector.addrIDToName(addrID).get
  lazy val toDeviceAddress:DeviceAddress = selector.addrIDToDeviceAddress(addrID).get
}

object UserDevice extends HasDBSelector {
  val conf = ConfigLoader.loadFile("config.xml")
  def fromConfig(cl:ConfigLoader):Seq[UserDevice] = {
    cl.users.map(new UserDevice(_))
  }

  lazy val devices:Seq[UserDevice] = fromConfig(conf)
  lazy val deviceAddresses:Seq[DeviceAddress] = devices.map(_.toDeviceAddress)
  lazy val addrIDs:Seq[Int] = devices.map(_.addrID)
  def addrIDToUserDevice(id:Int):UserDevice = {
    devices.find(_.addrID == id).get
  }
}

