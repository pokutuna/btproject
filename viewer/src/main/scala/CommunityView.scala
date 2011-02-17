package org.btproject.gui

import scala.swing._
import java.awt.Dimension
import javax.swing.UIManager
import edu.uci.ics.jung.graph._
import edu.uci.ics.jung.algorithms.layout._
import edu.uci.ics.jung.visualization._
import edu.uci.ics.jung.graph.util._
import edu.uci.ics.jung.algorithms.transformation._
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller
import edu.uci.ics.jung.visualization.renderers._
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse
import org.scalaquery.session._
import org.scalaquery.session.Database._
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.ql._
import org.scalaquery.ql.basic._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.ql.extended._
import org.btproject._
import org.btproject.model._
import org.btproject.graph._
import org.btproject.db._
import org.btproject.analysis._
import org.btproject.util.TimestampUtilImplicits._

object CommunityView extends SimpleSwingApplication with HasDBSelector {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

  def top = new MainFrame{
    title = "Community View"
    contents = new BorderPanel{
      preferredSize = new java.awt.Dimension(1200,800)
      add (new ScrollPane(graphPane), BorderPanel.Position.Center)
    }
  }

  def addChildrenEdges(graph:Graph[Node,IntEdge], parent:Int):Unit = {
    selector.db.withSession {
      val communityCounts = CliqueContains.where(_.clique is parent).list
      val children = communityCounts.map(_.inner)
      
      val thisChildren = children.filter { cc =>
        val c = Query(CliqueContains.where(_.inner is cc).count).first
        println(c)
        if (c <= 1) true else false
      }
      val parentName = PreCliques.where(_.cliqueID is parent).first.devString
      
      thisChildren.foreach { c =>
        val name = PreCliques.where(_.cliqueID is c).first.devString
        val node = UserNode(name)
        graph.addVertex(node)
        graph.addEdge(edges.takeEdge, UserNode(parentName), node)
      }
      thisChildren.foreach{ c => println(c);addChildrenEdges(graph, c)}
    }
  }
  val edges = new IntEdgeFactory  
  val treegraph = {
    val graph = new DelegateForest[Node, IntEdge]
    val root = JointNode("Root")
    graph.addVertex(root)

    selector.db.withSession {
      val communityCounts = (CommunityCounts.where(_.count > 0)).list
      val toplevels = communityCounts.filter { cc =>
        val c = Query(CliqueContains.where(_.inner is cc.preCliqueID).count).first
        if (c == 0) true else false
      }
      toplevels.foreach { top =>
        val name = PreCliques.where(_.cliqueID is top.preCliqueID).first.devString
        val n = UserNode(name)
        graph.addVertex(n)
        graph.addEdge(edges.takeEdge, root, n)
      }
      toplevels.foreach( t => addChildrenEdges(graph, t.preCliqueID))
    }
    graph
  }
  
  def graphPane = new FlowPanel{
//    val layout =new TreeLayout[Node, IntEdge](treegraph)
    val layout =new RadialTreeLayout[Node, IntEdge](treegraph)
    val panel = new VisualizationViewer(layout,new java.awt.Dimension(1200,800))
    panel.getRenderContext.setVertexLabelTransformer(new ToStringLabeller)
    panel.getRenderContext().setVertexShapeTransformer(GraphStyle.nodeShape)
    panel.getRenderContext().setVertexFillPaintTransformer(GraphStyle.nodeColor)
    import edu.uci.ics.jung.visualization.decorators.EdgeShape;
    panel.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
    panel.getRenderer.getVertexLabelRenderer.setPosition(Renderer.VertexLabel.Position.AUTO)
    panel.setGraphMouse(new DefaultModalGraphMouse)
    contents += Component.wrap(panel)
  }
}
