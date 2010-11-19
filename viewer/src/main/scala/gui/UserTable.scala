package org.btproject.gui

import scala.swing._
import scala.swing.GridBagPanel._
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.BasicStroke
import java.awt.Color
import javax.swing.JPanel

class UserTable {
  val panel = new BoxPanel(Orientation.Vertical) {
    for (n <- 1 to 10) contents += (new UserTimeline(n.toString)).panel
    minimumSize = new Dimension(200,200)
    size = new Dimension(200,size.height)
  }
}


class UserTimeline(name:String) {

  val nameLabel = new Label(name) { 
    border = new javax.swing.border.EtchedBorder    
  }

  val timeLine = new Panel {
    border = new javax.swing.border.EtchedBorder      
    override def paintComponent(g:Graphics2D):Unit = {
//      println (size)
      super.paintComponent(g)
      g.setColor(Color.WHITE)
      g.fillRect(0,0,100,100)
      g.setBackground(Color.BLACK)
    }
  }

  lazy val panel = new GridBagPanel {
    
    val c = new Constraints
    c.fill = Fill.Both
    c.anchor = Anchor.Center
    c.gridwidth = 1
    c.grid = (0,0)
    c.weightx = 0.2
    c.weighty = 0.5
    layout(nameLabel) = c
    
    c.gridwidth = 4
    c.weighty = 0.5    
    c.grid = (1,0)
    c.weightx = 0.8
    layout(timeLine) = c
  }

}

