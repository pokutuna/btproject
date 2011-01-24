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
  val start = "2010/11/4 12:00:00";  val end = "2010/11/4 13:00:00"
//    val start = "2010/11/4 9:00:00";    val end = "2010/11/4 18:00:00"

  val db = DBGraphSelector.getSelector

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
    println("  user: " + c.map(_.toString))
    println("  env_bt: " + dat.map(_.btDetects).reduceLeft(_ & _).map(db.addrToName(_)))
    println("  env_bt_sum: " + dat.map(_.btDetects).reduceLeft(_ | _).map(db.addrToName(_)))
    println("  env_wf: " + dat.map(_.wifiDetects).reduceLeft(_ & _).map(db.addrToName(_)))
    println("  env_wf_sum: " + dat.map(_.wifiDetects).reduceLeft(_ | _).map(db.addrToName(_)))

    println()
  }
  

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




