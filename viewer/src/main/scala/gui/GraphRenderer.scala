package org.btproject.gui

import edu.uci.ics.jung.graph._
import edu.uci.ics.jung.algorithms.layout._
import edu.uci.ics.jung.visualization.BasicVisualizationServer
import java.awt.Dimension
// import scala.collection.JavaConversions
// import scala.collection.JavaConverters
import edu.uci.ics.jung.graph.util._
import edu.uci.ics.jung.algorithms.transformation._
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller
import org.btproject.util.TimestampUtil
object GraphRenderer extends TimestampUtil{
  val graph:Graph[String,String] = new UndirectedSparseGraph[String,String]

//  FoldingTransformer.foldHypergraphEdges(graph,)


  import org.btproject.model._
  import org.btproject._

  val db = new DBGraphSelector(ConfigLoader.loadFile("config.xml"))
  val buf = db.getBDARecordsBetween("2010/11/4 10:00:00", "2010/11/4 11:00:00")
  println("log "+buf.length+" lines")

  import scala.collection.mutable.Map
  var vertexCache = Map[String,Boolean]()
  var nameCache = Map[String,String]()
  def solveName(addr:String):String = {
    nameCache.get(addr) match {
      case None =>
        val name = db.bdaToName(addr).getOrElse(addr)
        nameCache += (addr -> name)
        name
      case Some(name) => name
    }
  }
  
  val detects = buf.map(log => (log.logedBy, log.addr)).distinct
  println(detects)
  detects.foreach{ log =>
    val a = solveName(log._1)
    val b = solveName(log._2)
    graph.addVertex(a)
    graph.addVertex(b)
    graph.addEdge(a+"to"+b, a, b)
  }



//  for(n <- 1 to 20) graph.addVertex(n.toString)
//  import scala.util.Random
//  for(n <- 1 to 20) graph.addEdge(n,Random.nextInt(21).toString, Random.nextInt(21).toString())
  
  def getGraphPanel(d:Tuple2[Int,Int]):BasicVisualizationServer[String,String] = { 
    val panel = new BasicVisualizationServer(new KKLayout(graph),d)
    panel.getRenderContext.setVertexLabelTransformer(new ToStringLabeller)
    panel
  }

  implicit def tuple2Dimension(tuple:Tuple2[Int, Int]):Dimension =
    new Dimension(tuple._1, tuple._2)


}




