package org.btproject.gui

import scala.swing._
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.BasicStroke
import java.awt.Color
import javax.swing.JLabel

class UserTable {

  val panel = new BoxPanel(Orientation.Horizontal) {
    contents += new Label("pokutuna")
    contents += Component.wrap(new DashLine)
    border = new javax.swing.border.EtchedBorder
    repaint
  }

  class DashLine extends JLabel { //iterate each user
      //border = new javax.swing.border.EtchedBorder
      var stroke:BasicStroke = null
      override def paint(g:Graphics):Unit = {
        val g2 = g.asInstanceOf[Graphics2D]
        super.paint(g2);
        val ary = (1f::2f::1f::4f::3f::Nil).toArray
        if(stroke == null)
          stroke = new BasicStroke(5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, ary, 0f)
        g2.setStroke(stroke)
        g2.setColor(Color.BLACK)
        println(size)
        println("hoge")
        g2.drawLine(5, getHeight/2, getWidth-10, getHeight/2)
      }
    }
}
