package org.btproject.db

import scala.actors.Actor
import scala.actors.Actor._
import java.sql.Timestamp
import org.btproject._
import org.btproject.analysis._

case object EXIT
case object COMPLETE
case class TimeSpan(begin:Timestamp, end:Timestamp, window:Int)
  
class TimeManagerActor(workerSize:Int, schedules:List[(Timestamp,Timestamp,Int)]) extends Actor {

  val jobs = scala.collection.mutable.Queue(schedules:_*)
  val workers = (1 to workerSize toList).map{ new InsertCommunityActor(_, this) }
  workers.foreach {
    val job = jobs.dequeue()
    _ ! TimeSpan(job._1, job._2, job._3)
  }
  
  def act {
    loop {
      react {
        case COMPLETE =>
          val job = jobs.dequeue()
          sender ! TimeSpan(job._1, job._2, job._3)
        case EXIT => exit
        case _ => throw new RuntimeException("RootActor: received unknown message")
      }
    }
  }
  start
}

class InsertCommunityActor(id:Int, root:Actor) extends Actor with HasDBSelector {
  println("Worker " + id + "activated")
  def act {
    loop {
      react {
        case t:TimeSpan =>
          println(id + ": " + t.begin)
          val coms = CommunityExtractor.timeBetween(t.begin, t.end, t.window)
          coms.foreach(selector.addCommunityRecord(_))
          root ! COMPLETE
        case EXIT => exit
        case _ => throw new RuntimeException("[" + id + "] receive unknown message")
      }
    }
  }
  start
}
