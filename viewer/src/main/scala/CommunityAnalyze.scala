package org.btproject.analysis

import org.btproject.ConfigLoader
import org.btproject.db._
import org.btproject.util._
import org.btproject.model._
import scala.util.matching.Regex
import org.scalaquery.session._
import org.scalaquery.session.Database._
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.ql._
import org.scalaquery.ql.basic._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.ql.extended._
import java.sql.Timestamp

object CommunityAnalyze extends HasDBSelector {
  val excludeIDs = UserDevice.addrIDs.toSet

  def placeVariance(preCliqueID:Int):Double = {
    selector.db.withSession {
      val average = getAverageCommunity(preCliqueID)      
      val crs = CommunityRecords.where(_.preCliqueID is preCliqueID).list
      crs.foldLeft(0.0)((sum, rec) => sum + diffAverage(rec, average)) / (crs.size - 1)
    }
  }

  def placeVarianceIncludeOuter(preCliqueID:Int):Double = {
    selector.db.withSession {
      val average = getAverageIncludeOuterCommunity(preCliqueID)
      val q = for (c <- CliqueContains if c.inner is preCliqueID) yield c.clique
      val cliqueIDs = preCliqueID :: q.list      
      val crs = cliqueIDs.flatMap { cid =>
        CommunityRecords.where(_.preCliqueID is cid).list
      }
      crs.foldLeft(0.0)((sum, rec) => sum + diffAverage(rec, average)) /(crs.size - 1)
    }
  }

  def placeVarianceOnlyOuter(preCliqueID:Int):Double = {
    selector.db.withSession {
      val average = getAverageOnlyOuterCommunity(preCliqueID)
      val q = for (c <- CliqueContains if c.inner is preCliqueID) yield c.clique
      val cliqueIDs = q.list
      val crs = cliqueIDs.flatMap { cid =>
        CommunityRecords.where(_.preCliqueID is cid).list
      }
      crs.foldLeft(0.0)((sum, rec) => sum + diffAverage(rec, average)) /(crs.size - 1)
    }
  }

  def diffAverage(rec:CommunityRecord, average:Map[Int,Double]):Double =
    diffAverage(SerializedDevice.toIntSeq(rec.envBT + rec.envWF), average)
  
  def diffAverage(seq:Seq[Int], average:Map[Int,Double]):Double = {
    val diff = average.foldLeft(0.0){ (sum, i) =>
      if (seq.contains(i._1)) 1.0 - i._2 else i._2
      //if (seq.contains(i._1)) math.pow(1.0 - i._2, 2) else math.pow(0.0 - i._2, 2)
    }
    return diff
  }

  def getAverageIncludeOuterCommunity(preCliqueID:Int):Map[Int,Double] = {
    selector.db.withSession {
      val q = for (c <- CliqueContains if c.inner is preCliqueID) yield c.clique
      val cliqueIDs = preCliqueID :: q.list
      val longIntSeq = cliqueIDs.flatMap { cid =>
        val q = for (rec <- CommunityRecords if rec.preCliqueID is cid) yield rec.envBT ~ rec.envWF
        q.list.map(t => t._1 + t._2)
      }.map(SerializedDevice.toIntSeq(_))
      val size = longIntSeq.size
      val flat = longIntSeq.flatten
      val keys = flat.distinct.toSet -- excludeIDs
      val average = keys.map( k => (k -> flat.count(_ == k).toDouble / size)).toMap
      return average
    }
  }
  
  def getAverageOnlyOuterCommunity(preCliqueID:Int):Map[Int,Double] = {
    selector.db.withSession {
      val q = for (c <- CliqueContains if c.inner is preCliqueID) yield c.clique
      val cliqueIDs = q.list
      val longIntSeq = cliqueIDs.flatMap { cid =>
        val q = for (rec <- CommunityRecords if rec.preCliqueID is cid) yield rec.envBT ~ rec.envWF
        q.list.map(t => t._1 + t._2)
      }.map(SerializedDevice.toIntSeq(_))
      val size = longIntSeq.size
      val flat = longIntSeq.flatten
      val keys = flat.distinct.toSet -- excludeIDs
      val average = keys.map( k => (k -> flat.count(_ == k).toDouble / size)).toMap
      return average
    }
  }
  
  def getAverageCommunity(preCliqueID:Int):Map[Int,Double] = {
    selector.db.withSession {
      val q = for (rec <- CommunityRecords if rec.preCliqueID is preCliqueID) yield rec.envBT ~ rec.envWF
      val records = q.list
      val size = records.size      
      val longDevString = records.foldLeft("")((a,b) => a + b._1 + b._2)
      val devSeq = SerializedDevice.toIntSeq(longDevString)
      val keys = devSeq.distinct.toSet -- excludeIDs
      val average = keys.map( k => (k -> devSeq.count(_ == k).toDouble / size)).toMap
      return average
    }
  }

  def main(args: Array[String]) = {
    selector.db.withSession {

      val cc = CommunityCounts.where(_.count > 0).list
      val res = cc.map { c =>
        val dist = placeVariance(c.preCliqueID)
        val countOuter = ParentalCommunityCounts.where(_.preCliqueID is c.preCliqueID).first.countParental
        val distOuter = placeVarianceIncludeOuter(c.preCliqueID)
        val distOnlyOuter = placeVarianceOnlyOuter(c.preCliqueID)
        val tup = (c.preCliqueID, c.count, if(dist.isNaN) 0.0 else dist,
                   countOuter, if(distOuter.isNaN) 0.0 else distOuter, 
                   countOuter - c.count, if(distOnlyOuter.isNaN) 0.0 else distOnlyOuter)
        println(tup)
        tup
      }
      val str = res.foldLeft("")( (s,t) => s + t._1 + "," + t._2 + "," + t._3 + "," + t._4 + "," + t._5 + "," + t._6 + "," + t._7 + "\n") 

      FileWrapper("community_dist.csv").write(str)
    }


  }
}










