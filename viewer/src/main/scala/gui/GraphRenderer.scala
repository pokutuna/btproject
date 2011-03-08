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
import org.btproject.util.TimestampUtilImplicits._
import org.btproject.model._
import org.btproject.graph._
import org.btproject.analysis._


object GraphRenderer {

//  val start = "2010/11/4 10:10:00";  val end = "2010/11/4 10:40:00"
//  val start = "2010/10/18 0:00:00";  val end = "2010/10/31 00:00:00"
  val start = "2010/11/4 12:00:00";  val end = "2010/11/4 12:20:00"  
//    Val start = "2010/11/4 9:00:00";    val end = "2010/11/4 18:00:00"

  val t1 = System.currentTimeMillis()
  val datas = TimeSpanDetect.timeBetween(start,end)
  println(datas.flatMap(_.detectDevices))
  val t3 = System.currentTimeMillis()
  println((t3-t1)/1000 + "sec")
//  val graph = GraphBuilder.buildFromTimeSpanDetects(datas)
  val graph = getSampleGraph
  val t2 = System.currentTimeMillis()
  println((t2 - t3)/1000 + "sec")
  import org.btproject.analysis.TimeSeries
  //TimeSeries.printCommunities(start,end)

  def getGraphPanel(d:Dimension):BasicVisualizationServer[Node,Edge] = { 
    val panel = new VisualizationViewer(new KKLayout(graph),d)
    
//    panel.getRenderContext.setVertexLabelTransformer(new ToStringLabeller)
    import edu.uci.ics.jung.visualization.decorators.EdgeShape;
    panel.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
    
    panel.getRenderContext().setVertexShapeTransformer(GraphStyle.nodeShape)
    panel.getRenderContext().setVertexFillPaintTransformer(GraphStyle.nodeColor)
    panel.getRenderer.getVertexLabelRenderer.setPosition(Renderer.VertexLabel.Position.AUTO)
    panel.setGraphMouse(new DefaultModalGraphMouse)
    return panel
  }

  def getSampleGraph:Graph[Node,Edge] = {
    val graph = new UndirectedSparseGraph[Node,Edge]
    val edge = new IntEdgeFactory
    val e:(Int, Int) => Unit = { (a, b) =>
      graph.addEdge(edge.takeEdge, UserNode(a.toString()), UserNode(b.toString()))
    }
    
    for (n <- 0 to 4) graph.addVertex(UserNode(n.toString()))
    e(0, 1); e(0,2); e(0,3); e(0,4); e(1,2); e(1,3); e(1,4)
    e(2,3); e(2,4); e(4,3); //e(0,5); e(0,6);;e(6,7);e(5,6);e(5,4)
    return graph
  }
}




