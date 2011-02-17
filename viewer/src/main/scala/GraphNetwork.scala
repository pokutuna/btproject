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

case class UserNode(label:String)(implicit degree:Int = 1) extends Node {
  def shape:Shape = new Ellipse2D.Double(-10, -10, 20, 20)
  def color:Paint = if (degree > 0) Color.RED else Color.GRAY
}

case class WifiNode(label:String) extends Node {
  def shape:Shape = new Rectangle2D.Double(-5, -5, 10, 10)
  def color:Paint = Color.GREEN
}

case class BTNode(label:String) extends Node {
  def shape:Shape = new Ellipse2D.Double(-5,-5,10,10)
  def color:Paint = Color.BLUE
}

case class JointNode(label:String = "") extends Node {
  def shape:Shape = new Ellipse2D.Double(-3, -3, 6, 6)
  def color:Paint = Color.GRAY
}

case class ManualNode(label:String, width:Int, height:Int, color:Paint) extends Node {
  def shape:Shape = new Ellipse2D.Double(-width/2, -height/2, width, height)
}
  
case class IntEdge(label:String, value:Int) extends Edge
