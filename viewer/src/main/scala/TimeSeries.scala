package org.btproject.analysis

import org.btproject.util.TimestampUtil
import org.btproject.model._
import org.btproject.graph._
import org.btproject.analysis._
import org.btproject.util.TimestampUtilImplicits._

object TimeSeries extends {
  val db = DBGraphSelector.getSelector

  def printCommunities(start:String, end:String):Unit = { 
    val datas = UserDataBuilder.timeBetween(start,end)
    val graph = GraphBuilder.buildFromUserDatas(datas)

    //Clique
    val ce = new CliqueExtractor(graph)
    import scala.collection.JavaConversions._
    val users = graph.getVertices.filter(_.isInstanceOf[UserNode])
    val cliques = ce.localMaximums(2,users).toList
    println("Cliqeus:" + cliques)

    cliques.foreach{ c =>
      val dat = datas.filter{ d => c.map(_.toString).contains(d.name)}
      println("communitie")
      println("user: " + c.map(_.toString))
      println("  env_bt: " + dat.map(_.btDetects).reduceLeft(_ & _).map(db.addrToName(_)))
      println("  env_bt_sum: " + dat.map(_.btDetects).reduceLeft(_ | _).map(db.addrToName(_)))
      println("  env_wf: " + dat.map(_.wifiDetects).reduceLeft(_ & _).map(db.addrToName(_)))
      println("  env_wf_sum: " + dat.map(_.wifiDetects).reduceLeft(_ | _).map(db.addrToName(_)))
      println()
    }
  }

  def main(args: Array[String]) = {
    TimeSeries.printCommunities(
    "2010/11/4 11:45:00",
    "2010/11/4 12:15:00")
    println("-----")
    TimeSeries.printCommunities(
    "2010/11/4 12:00:00",
    "2010/11/4 12:30:00")
    println("-----")
    TimeSeries.printCommunities(
    "2010/11/4 12:15:00",
    "2010/11/4 12:45:00")
  }
}
