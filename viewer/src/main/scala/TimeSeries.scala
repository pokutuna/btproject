package org.btproject.analysis

import org.btproject.util.TimestampUtil
import org.btproject.model._
import org.btproject.db._
import org.btproject.graph._
import org.btproject.analysis._
import org.btproject.util.TimestampUtilImplicits._

object TimeSeries extends HasDBSelector {

  def printCommunities(start:String, end:String):Unit = { 
    val datas = TimeSpanDetect.timeBetween(start,end)
    val cliques = CliqueExtractor.extractFromTimeSpanDetects(2, datas)
    println("Cliqeus:" + cliques.map{ _.map(selector.addrIDToName(_).get)})


    val records = cliques.map{ c =>
      val dat = datas.filter{ d => c.contains(d.addrID) }
      println("clique: " + c.map(selector.addrIDToName(_).get))
      val preCID = selector.devStringToPreCliqueID(SerializedDevice.mkString(c.toSeq)).get
      println("cliqueDevString: " + SerializedDevice.mkString(c.toSeq) + " => " +preCID)
      
      val btdevs = dat.map(_.btDevices.map(_.addrID.get).toSet).reduceLeft(_ & _) -- c
      val envBTID = SerializedDevice.mkString(btdevs.toSeq)
      val wifidevs = dat.map(_.wifiDevices.map(_.addrID.get).toSet).reduceLeft(_ & _)
      val envWFID = SerializedDevice.mkString(wifidevs.toSeq)
      println("bt: " + btdevs.map(selector.addrIDToName(_).get) + " => " +envBTID)
      println("wf: " + wifidevs.map(selector.addrIDToName(_).get) + " => " + envWFID)
      println("")
      CommunityRecord(start, 60, preCID, envBTID, envWFID)
    }
    println(records)

    println("---")

    println(CommunityExtractor.timeBetween(start,end, 60))

    /*
    import edu.uci.ics.jung.algorithms.scoring._
    import scala.collection.JavaConversions._
    val bet = new BetweennessCentrality(graph)
    val rankedBetweeness = graph.getVertices.toSeq.sortWith(bet.getVertexScore(_).doubleValue > bet.getVertexScore(_).doubleValue())
    rankedBetweeness.take(5).foreach{ x => println(x + ": " + bet.getVertexScore(x))}
    import org.btproject.gui.SimpleGraphViewer
    (new SimpleGraphViewer("", graph)).startup(Array[String]())
    */
  }

  def main(args: Array[String]) = {
    import org.btproject.util._
    val span = new TimeSpanGenerator("2010/11/4 12:00:00", 60, 60)
    println("addrIDs" + UserDevice.addrIDs)
    for(n <- 1 to 3){
      val time = span.getSpan()
      println(time._1 + " - " + time._2) 
      TimeSeries.printCommunities(time._1, time._2)
      println("-----")
    }
  }
}
