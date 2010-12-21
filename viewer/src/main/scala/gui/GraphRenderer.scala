package org.btproject.gui

import edu.uci.ics.jung.graph._
import edu.uci.ics.jung.algorithms.layout._
import edu.uci.ics.jung.visualization._
import java.awt.Dimension
import edu.uci.ics.jung.graph.util._
import edu.uci.ics.jung.algorithms.transformation._
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller
import edu.uci.ics.jung.visualization.renderers._
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse
import org.btproject.util.TimestampUtil
import org.btproject.graph._



object GraphRenderer extends TimestampUtil{

  import org.btproject.model._
  import org.btproject._

  val graph:Graph[Node,Edge] = new UndirectedSparseGraph[Node,Edge]
  println("loading")
  val db = new DBGraphSelector(ConfigLoader.loadFile("config.xml"))
//  val start = "2010/11/4 10:10:00"
//  val end = "2010/11/4 10:40:00"
//  val start = "2010/11/4 12:00:00" 
//  val end = "2010/11/4 13:00:00"
    val start = "2010/11/4 9:00:00"
    val end = "2010/11/4 18:00:00"
  val buf = db.getBDADetectsBetween(start,end)
  println("log "+buf.length+" lines")

  import scala.collection.mutable.Map
  val nodes = Map[String,Node]()  
  def getNode(name:String)(f:String => Node):Node = {
    nodes.get(name) match {
      case Some(n) => n
      case None => f(name)//new UserNode(db.addrToName(name))
    }
  }
  val detector = buf.map(_.logedBy)
  var i = 0
  buf.foreach{ log =>
    val a = getNode(log.logedBy)(name => new UserNode(db.addrToName(name)))
    val b = getNode(log.addr)(name =>
      if(detector.contains(name))
        new UserNode(db.addrToName(name))
        else OtherNode(db.addrToName(name)))
    graph.addVertex(a)
    graph.addVertex(b)
    i+=1
    graph.addEdge(IntEdge(i.toString), a, b)
  }

  val wifi = db.getWifiDetectsBetween(start,end)
  println("log "+wifi.length+" lines")
  wifi.foreach{ log =>
    val a = getNode(log.logedBy)(name => new UserNode(db.addrToName(name)))
    val b = getNode(log.addr)(name => new WifiNode(db.addrToName(name)))
    i+=1
    graph.addEdge(IntEdge(i.toString), a, b)
  }

/*  val ce = new CliqueExtractor(graph)
  import scala.collection.JavaConversions._
  val users = graph.getVertices.filter(_.isInstanceOf[UserNode])
  println(ce.localMaximums(3,users).toList)
*/

  def getGraphPanel(d:Dimension):BasicVisualizationServer[Node,Edge] = { 
    val panel = new VisualizationViewer(new KKLayout(graph),d)
    panel.getRenderContext.setVertexLabelTransformer(new ToStringLabeller)
    panel.getRenderContext().setVertexShapeTransformer(GraphStyle.nodeShape)
    panel.getRenderContext().setVertexFillPaintTransformer(GraphStyle.nodeColor)
    panel.getRenderer.getVertexLabelRenderer.setPosition(Renderer.VertexLabel.Position.AUTO)
    panel.setGraphMouse(new DefaultModalGraphMouse)
    return panel
  }


}




