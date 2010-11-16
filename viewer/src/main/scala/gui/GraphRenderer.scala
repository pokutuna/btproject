package org.btproject.gui

import edu.uci.ics.jung.graph._
import edu.uci.ics.jung.algorithms.layout._
import edu.uci.ics.jung.visualization.BasicVisualizationServer
import java.awt.Dimension
import scala.collection.JavaConversions
import scala.collection.JavaConverters
import edu.uci.ics.jung.graph.util._
import edu.uci.ics.jung.algorithms.transformation._

object GraphRenderer {
  val graph:Graph[Int,Int] = new UndirectedSparseGraph[Int,Int]

//  FoldingTransformer.foldHypergraphEdges(graph,)

  /*
  import org.btproject.model._
  import org.btproject._
  val db = new DBGraphSelector(ConfigLoader.loadFile("config.xml"))
  println("log "+db.sample.length+" lines")

  db.sample.foreach{  log =>
    val a = db.bdaToName(log.logedBy).getOrElse(log.logedBy)
    val b = db.bdaToName(log.bda).getOrElse(log.bda)
    graph.addVertex(a)
    graph.addVertex(b)
    graph.addEdge(a+"to"+b, a, b)
                  }
  
  */

  for(n <- 1 to 20) graph.addVertex(n)
  import scala.util.Random
  for(n <- 1 to 20) graph.addEdge(n,Random.nextInt(21), Random.nextInt(21))
  
  def getGraphPanel(d:Tuple2[Int,Int]):BasicVisualizationServer[Int,Int] = 
    new BasicVisualizationServer(new KKLayout(graph),d)

  implicit def tuple2Dimension(tuple:Tuple2[Int, Int]):Dimension =
    new Dimension(tuple._1, tuple._2)


}




