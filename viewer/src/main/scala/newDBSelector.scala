package org.btproject.db

import org.btproject.ConfigLoader
import org.btproject.db._
import org.btproject.util.TimestampUtil
import org.scalaquery.session._
import org.scalaquery.session.Database._
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.ql._
import org.scalaquery.ql.basic._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.ql.extended._
import java.sql.Timestamp

object newDBSelector {
  lazy val selector = new newDBSelector(ConfigLoader.loadFile("config.xml"))
  def getSelector:newDBSelector = selector
}

class newDBSelector(val config:ConfigLoader) {
  val db:Database = DBConnector(config)

  def addressToName(address:String):Option[String] = {
    addressToAddrID(address) match {
      case Some(id) => addrIDToName(id)
      case None => None
    }
  }

  private val cacheForAddrIDToDeviceAddress = scala.collection.mutable.Map[Int,Option[DeviceAddress]]()
  def addrIDToDeviceAddress(id:Int):Option[DeviceAddress] = {
    def takeFromDB(id:Int):Option[DeviceAddress] =
      db.withSession { DeviceAddresses.where(_.addrID is id).firstOption }
    
    cacheForAddrIDToDeviceAddress.get(id) match {
      case Some(n) => n
      case None =>
        val da = takeFromDB(id)
        if (da != None) cacheForAddrIDToDeviceAddress += (id -> da)
        return da
    }
  }

  private val cacheForAddrIDToAddress = scala.collection.mutable.Map[Int,Option[String]]()
  def addrIDToAddress(id:Int):Option[String] = {
    def takeFromDB(id:Int) = { 
      db.withSession {
        val q = for ( d <- DeviceAddresses if d.addrID is id) yield d.address
        q.firstOption
      }
    }

    cacheForAddrIDToAddress.get(id) match {
      case None =>
        val address = takeFromDB(id)
        if (address != None) cacheForAddrIDToAddress += (id -> address)
        return address
      case Some(n) => return n
    }
  }

  private val cacheForAddrIDToName = scala.collection.mutable.Map[Int,Option[String]]()
  def addrIDToName(id:Int):Option[String] = {
    def takeFromDB(id:Int):Option[String] = { 
      db.withSession {
        val q = for ( d <- DeviceNames if d.addrID is id) yield d.name
        q.firstOption
      }
    }

    cacheForAddrIDToName.get(id) match {
      case None =>
        val name = takeFromDB(id)
        if (name != None) cacheForAddrIDToName += (id -> name)
        return name
      case Some(n) => return n
    }
  }

  private val cacheForAddressToAddrID = scala.collection.mutable.Map[String,Option[Int]]()
  def addressToAddrID(address:String):Option[Int] = {
    def takeFromDB(str:String):Option[Int] = {
      db.withSession {
        val q = for (inv <- InvertedDeviceAddresses if inv.address is str) yield inv.addrID
        q.firstOption
      }
    }
    
    cacheForAddressToAddrID.get(address) match {
      case None =>
        val id = takeFromDB(address)
        if (id != None) cacheForAddressToAddrID += (address -> id)
        return id
      case Some(n) => return n
    }
  }

  private val cacheForDevStringToPreCliqueID = scala.collection.mutable.Map[String,Option[Int]]()
  def devStringToPreCliqueID(devString:String):Option[Int] = {
    def takeFromDB(str:String):Option[Int] = {
      db.withSession {
        val q = for (pc <- PreCliques if pc.devString is str) yield pc.cliqueID
        q.firstOption()
      }
    }

    cacheForDevStringToPreCliqueID.get(devString) match {
      case None =>
        val id = takeFromDB(devString)
        if (id != None) cacheForDevStringToPreCliqueID += (devString -> id)
        return id
      case Some(n) => return n
    }
  }

  def addDeviceAddress(address:String, deviceType:DeviceType.Value):Unit = {
    if (addressToAddrID(address) == None) {
      val dev = DeviceAddress(None, address, deviceType)
      db.withSession {      
        DeviceAddresses.forInsert.insert(dev)
        val id:Int = (for (d <- DeviceAddresses if d.address is address) yield d.addrID).first
        InvertedDeviceAddresses insert (address, id)
        cacheForAddressToAddrID += (address -> Some(id))
      }
    }
  }

  def addDeviceName(address:String, name:String) = {
    addressToAddrID(address) match {
      case None => throw new RuntimeException("This device(" + address + ") wasn't inserted")
      case Some(id) =>
        db.withSession { 
          DeviceNames.where(_.addrID is id).firstOption match {
            case None => DeviceNames insert DeviceName(id, name)
            case Some(dn) if dn.name == "" || dn.name == "n/a." =>
              val q = for(d <- DeviceNames if d.addrID is id) yield d.name
              q.update(name)            
            case _ =>
          }
        }
     }
  }

  def addDevice(address:String, deviceType:DeviceType.Value, name:String) = {
    addDeviceAddress(address, deviceType)
    addDeviceName(address, name)
  }

  def getDetectRecord(id:Int, when:Timestamp):Option[DetectRecord] = {
    db.withSession {
      DetectRecords.where( d => (d.addrID is id) && (d.time is when)).firstOption
    }
  }
  
  def addDetectRecord(record:DetectRecord) = {
    db.withSession{ 
      getDetectRecord(record.addrID, record.time) match {
        case None =>
          DetectRecords.forInsert.insert(record)
        case Some(d) =>
          val sdev = SerializedDevice.mergeDeviceString(record.devString, d.devString)
          val q = for (d <- DetectRecords if (d.addrID is record.addrID) && (d.time is record.time)) yield d.devString
          q.update(sdev)
          println(d + " updated to " + sdev)
      }
    }
  }

//  def addCommunityRecord()
}

trait AddrIDMapping extends HasDBSelector {
  val addrID:Int
  lazy val toDeviceAddress:Option[DeviceAddress] = selector.addrIDToDeviceAddress(addrID)
  lazy val deviceName:Option[String] = selector.addrIDToName(addrID)
  lazy val address:Option[String] = selector.addrIDToAddress(addrID)
}









