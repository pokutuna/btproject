package org.btproject.gui

import scala.swing._
import org.btproject.model._
import org.btproject.graph._
import org.btproject.analysis._
import javax.swing.UIManager
import edu.uci.ics.jung.graph.util._
import edu.uci.ics.jung.algorithms.transformation._
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller
import edu.uci.ics.jung.visualization.renderers._
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse
import edu.uci.ics.jung.graph._
import edu.uci.ics.jung.algorithms.layout._
import edu.uci.ics.jung.visualization._

class SimpleGraphViewer(name:String, graph:Graph[Node,Edge]) extends SimpleSwingApplication {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
  val dim = new java.awt.Dimension(800,600)
  def top = new MainFrame{
    title = name
    contents = new BorderPanel{
      preferredSize = dim
      add (new ScrollPane(graphPane), BorderPanel.Position.Center)
    }
  }
  
  def graphPane = new FlowPanel{
    val panel = new VisualizationViewer(new KKLayout(graph),dim)
    panel.getRenderContext.setVertexLabelTransformer(new ToStringLabeller)
    panel.getRenderContext().setVertexShapeTransformer(GraphStyle.nodeShape)
    panel.getRenderContext().setVertexFillPaintTransformer(GraphStyle.nodeColor)
    panel.getRenderer.getVertexLabelRenderer.setPosition(Renderer.VertexLabel.Position.AUTO)
    panel.setGraphMouse(new DefaultModalGraphMouse)

    contents += Component.wrap(panel)
  }
}
