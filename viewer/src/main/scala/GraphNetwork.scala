package org.btproject.graph

import org.btproject.gui._
import java.awt._
import java.awt.geom._

trait GraphNetwork {

}

trait Node extends NodeStyle {
  val label:String
}

trait Edge extends EdgeStyle {
  val label:String
}

case class UserNode(label:String) extends Node {
  def shape:Shape = new Ellipse2D.Double(-10, -10, 20, 20)
  def color:Paint = Color.RED
}

case class WifiNode(label:String) extends Node {
  def shape:Shape = new Rectangle2D.Double(-5, -5, 10, 10)
  def color:Paint = Color.GREEN
}

case class OtherNode(label:String) extends Node {
  def shape:Shape = new Ellipse2D.Double(-5,-5,10,10)
  def color:Paint = Color.BLUE
}

case class IntEdge(label:String) extends Edge
