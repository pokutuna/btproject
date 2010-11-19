package org.btproject.gui

import org.apache.commons.collections15.Transformer;
import java.awt._
import java.awt.geom._

object GraphStyle { 
  val nodeShape = new Transformer[Node,Shape]{
    override def transform(n:Node):Shape = n.shape
  }
  val nodeColor = new Transformer[Node,Paint]{
    override def transform(n:Node):Paint = n.color
  }
}

trait Node {
  val label:String
  def shape:Shape
  def color:Paint
  override def toString:String = label  
}

case class UserNode(label:String) extends Node {
  def shape:Shape = new Ellipse2D.Double(-10, -10, 20, 20)
  def color:Paint = Color.RED
}

case class WifiNode(label:String) extends Node {
  def shape:Shape = new Rectangle2D.Double(-10, -10, 20, 20)
  def color:Paint = Color.GREEN
}

case class OtherNode(label:String) extends Node {
  def shape:Shape = new Ellipse2D.Double(-5,-5,10,10)
  def color:Paint = Color.BLUE
}

