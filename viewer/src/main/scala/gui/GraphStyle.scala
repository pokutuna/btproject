package org.btproject.gui

import org.apache.commons.collections15.Transformer;
import java.awt._
import java.awt.geom._
import org.btproject.graph._

object GraphStyle { 
  val nodeShape = new Transformer[Node,Shape]{
    override def transform(n:Node):Shape = n.shape
  }
  val nodeColor = new Transformer[Node,Paint]{
    override def transform(n:Node):Paint = n.color
  }
}

trait NodeStyle {
  val label:String
  def shape:Shape
  def color:Paint
  override def toString:String = label  
}


trait EdgeStyle {
  
}
