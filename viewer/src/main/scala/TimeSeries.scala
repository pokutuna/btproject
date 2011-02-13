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
    val cliques = CliqueExtractor.extractFromTimeSpanDetects(1, datas)
    println("Cliqeus:" + cliques.map{ _.map(selector.addrIDToName(_).get)})

    /*
    cliques.foreach{ c =>
      val dat = datas.filter{ d => c.map(_.toString).contains(d.name)}
      println("clique: " + c.map(_.toString))
      println("bt: " + dat.map(_.btDetects).reduceLeft(_ & _).map(db.addrToName(_)))
//      println("bt_sum: " + dat.map(_.btDetects).reduceLeft(_ | _).map(db.addrToName(_)))
      println("wf: " + dat.map(_.wifiDetects).reduceLeft(_ & _).map(db.addrToName(_)))
//      println("wf_sum: " + dat.map(_.wifiDetects).reduceLeft(_ | _).map(db.addrToName(_)))
      println()
    }

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

    for(n <- 1 to 3){
      val time = span.getSpan()
      print(time._1 + " - " + time._2) 
      TimeSeries.printCommunities(time._1, time._2)
      println("-----")
    }
  }
}
