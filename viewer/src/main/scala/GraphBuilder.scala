package org.btproject.graph

import scala.collection._
import org.btproject.model._
import org.btproject.graph._
import edu.uci.ics.jung.graph._

//グラフ構造つくる
class GraphBuilder[Node,Edge] {

  val graph:Graph[Node,Edge] = new UndirectedSparseGraph[Node,Edge]
  val nodes = mutable.Map[String,Node]()

  def getNode(name:String):Option[Node] = {
    nodes.get(name)
  }
  
  
}
