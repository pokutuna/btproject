package org.btproject.analysis

import javax.swing.UIManager
import org.btproject.util.TimestampUtil
import scala.swing._
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
import org.btproject.gui._
import org.btproject.model._
import org.btproject._

object ClusteringViewer extends SimpleSwingApplication with TimestampUtil {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
  
  def top = new MainFrame{
    title = "ClusterViewer"
    contents = new BorderPanel {
      preferredSize = new java.awt.Dimension(800,800)
      add(new ScrollPane(graphPanel), BorderPanel.Position.Center)
    }
  }

  val graphPanel = new FlowPanel {
    val graph = new DelegateForest[Node,IntEdge]

    //addNodes
//    val start = "2010/11/4 10:00:00"
//    val end = "2010/11/4 11:00:00"
    val start = "2010/11/4 9:00:00"
    val end = "2010/11/4 18:00:00"

    val users = UserDataBuilder.timeBetween(start,end)
    val db = DBGraphSelector.getSelector
    val edge = new IntEdgeFactory
    val multiplicity = new UserMultiplicity(users)
    multiplicity.cluster

    multiplicity.clusterPool

    val t = JointNode("top")
    graph.addVertex(t)
    setNodes(multiplicity.clusterPool.head, t)

    def setNodes(cluster:MultiplicityCluster, parent:Node):Unit = {
      if (cluster.users.size == 1){
        val n = UserNode(db.addrToName(cluster.users.head.addr))
        graph.addVertex(n)
        graph.addEdge(edge.takeEdge, parent,n)
      } else {
        val p = JointNode(edge.value.toString)
        graph.addEdge(edge.takeEdge, parent, p)
        cluster.innerCluster.foreach{
          setNodes(_, p)
        }
      }
    }


    //layout
    val layout = new TreeLayout[Node,IntEdge](graph)
//    val layout = new KKLayout(graph)
    val panel = new VisualizationViewer[Node,IntEdge](layout, new Dimension(1000,1000))
    panel.getRenderContext.setVertexLabelTransformer(new ToStringLabeller)
    panel.getRenderContext().setVertexShapeTransformer(GraphStyle.nodeShape)
    panel.getRenderContext().setVertexFillPaintTransformer(GraphStyle.nodeColor)
    panel.getRenderer.getVertexLabelRenderer.setPosition(Renderer.VertexLabel.Position.AUTO)
    panel.setGraphMouse(new DefaultModalGraphMouse)

    import edu.uci.ics.jung.visualization.decorators.EdgeShape;
    panel.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
    
    contents += Component.wrap(panel)
  }
}
