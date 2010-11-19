package org.btproject.gui

import scala.swing._

import javax.swing.UIManager
import org.btproject.ConfigLoader
import org.btproject.model._

object GUIRoot extends SimpleSwingApplication { 
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    
  def top = new MainFrame{
    title = "LogViewer"
    contents = new BorderPanel {
      preferredSize = new java.awt.Dimension(800,600)
      add ((new UserTable()).panel, BorderPanel.Position.West)
      add (new ScrollPane(right), BorderPanel.Position.Center)
//      contents += ((new UserTable).panel, 
//      contents += (new ScrollPane(right))
    }
  }

  val right = new FlowPanel {
    border = new javax.swing.border.EtchedBorder
    contents += Component.wrap(GraphRenderer.getGraphPanel(new Dimension(1000,1000)))
  }

}
