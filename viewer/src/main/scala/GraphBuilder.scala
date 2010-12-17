package org.btproject.graph

import scala.collection.mutable.Map
import org.btproject.model._
import org.btproject.graph._
import edu.uci.ics.jung.graph._

//グラフ構造つくる
class GraphBuilder[V,E] {

  val graph:Graph[V,E] = new UndirectedSparseGraph[V,E]
  val vertexes = Map[String,Node]()


}
