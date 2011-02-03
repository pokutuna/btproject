package org.btproject.analysis

import org.btproject.db._
import org.btproject.model._
import org.btproject.util._
import org.btproject.util.TimestampUtilImplicits._
import java.sql.Timestamp
import scala.collection.mutable.Map

object ChiSquareExample {

  def countOccuring(data:Iterable[UserData]):Unit = {
    import scala.util.control.Exception._
    allCatch opt(data.head.detects) match {
      case Some(detects) =>
        for (dev <- detects) count(dev) = count.getOrElse(dev,0) + 1
        for { devA <- detects
              devB <- detects if devA != devB } coOccurring(Set(devA,devB)) = coOccurring.getOrElse(Set(devA,devB),0) + 1
      case None => 
    }
  }

  def calcChiSquare(coOccuring:Map[Set[String],Int], count:Map[String,Int], size:Int):Map[Set[String],Double] = {
    val dest = Map[Set[String],Double]()
    for((k,v) <- coOccuring){
      val a = v
      val b = count.getOrElse(k.head, 0) - a
      val c = count.getOrElse(k.last, 0) - a
      val d = size - (a + b + c)
      
      val diff = (a * d - b * c) * (a * d - b * c)
      val expect = ((a + b) * (a + c) * (b + d) * (c + d)).toDouble
      val chi = size * diff / expect
      dest(k) = chi
    }
    return dest
  }
  
  val db = DBGraphSelector.getSelector
  val bda = "70:71:BC:21:11:1E"
  import scala.collection.mutable.Map
  val count = Map[String, Int]()
  val coOccurring = Map[Set[String], Int]()
  var windowSize = 0

  
  def main(args: Array[String]) = {
    val spanGen = new TimeSpanGenerator("2010/11/4 8:15:00", 30, 15)
    
    for (n <- 1 to 41) {
      windowSize += 1
      val span = spanGen.getSpan()
      println(span)
      val uData = UserDataBuilder.timeBetween(span._1, span._2)(bda)
      countOccuring(uData)
    }
    //output
    println(windowSize)
    for ((k,v) <- count) println(db.addrToName(k) + " : " + v)
    val a = calcChiSquare(coOccurring, count, windowSize)
    val res = a.toSeq.sortWith(_._2 > _._2).map{p =>
      (p._1.map(db.addrToName(_)), p._2)  }
    res foreach { p => println(p._1.head + " & " + p._1.last + " : " + p._2)}

    for(day <- 5 to 11) {
      val gen = new TimeSpanGenerator("2010/11/"+day+" 8:15:00", 30, 15)
      for (n <- 1 to 41) {
        windowSize += 1
        val span = gen.getSpan()
        println(span)
        val uData = UserDataBuilder.timeBetween(span._1, span._2)(bda)
        countOccuring(uData)
      }
    }
    println(windowSize)    
    for ((k,v) <- count) println(db.addrToName(k) + " : " + v)
    val all = calcChiSquare(coOccurring, count, windowSize)
    val allres = a.toSeq.sortWith(_._2 > _._2).map{p =>
      (p._1.map(db.addrToName(_)), p._2)  }
    allres foreach { p => println(p._1.head + " & " + p._1.last + " : " + p._2)}
  }
}

