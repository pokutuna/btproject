package org.btproject.gui

import scala.swing._

import javax.swing.UIManager
import org.btproject.ConfigLoader
import org.btproject.model._


object LogViewer extends SimpleSwingApplication { 
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

  val ut = (new UserTable).panel
  ut.repaint
  def top = new MainFrame{
    title = "LogViewer"
    contents = new GridPanel(1,2) {
      preferredSize = new java.awt.Dimension(800,600)
      contents += ut
      contents += Component.wrap(GraphRenderer.getGraphPanel((400,600)))
    }
  }

  val left = new FlowPanel{
    val label = new Label("left panel")
    border = new javax.swing.border.EtchedBorder
    contents += label
  }

  val right = new FlowPanel {
    val label = new Label("right panel")
    border = new javax.swing.border.EtchedBorder    
    contents += Component.wrap(GraphRenderer.getGraphPanel((400,300)))
  }

  override def main(args: Array[String]) = {
    Swing.onEDT { startup(args) }
    val cl = ConfigLoader.loadFile("config.xml")
    new DBGraphSelector(cl)
  }
}
