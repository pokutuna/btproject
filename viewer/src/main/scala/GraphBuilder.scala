package org.btproject.graph

import scala.collection._
import org.btproject.model._
import org.btproject.graph._
import org.btproject.analysis._
import edu.uci.ics.jung.graph._


//グラフ構造つくる
object GraphBuilder {
  def buildFromUserDatas(datas:Iterable[UserData]):Graph[Node,Edge] = {
    val db = DBGraphSelector.getSelector
    val edge = new IntEdgeFactory
    val graph:Graph[Node,Edge] = (new GraphBuilder).graph

    val detector = datas.map(_.addr).toList
    datas foreach{ d =>
      val n = UserNode(db.addrToName(d.addr))
      graph.addVertex(n)
                  
      d.btDetects.foreach{ addr =>
        val o = if(detector.contains(addr)) UserNode(db.addrToName(addr)) else OtherNode(db.addrToName(addr))
        graph.addVertex(o)
        graph.addEdge(edge.takeEdge, n, o)
      }
                  
      d.wifiDetects.foreach{ addr =>
        val o = WifiNode(db.addrToName(addr))
        graph.addVertex(o)
        graph.addEdge(edge.takeEdge, n, o)
      }
    }
    graph
  }
}

class GraphBuilder[Node,Edge] {

  val graph:Graph[Node,Edge] = new UndirectedSparseGraph[Node,Edge]
  val nodes = mutable.Map[String,Node]()

  def getGraph:Graph[Node,Edge] = graph
}

class IntEdgeFactory {
  var value = -1
  def takeEdge:IntEdge = {
    value += 1
    IntEdge("", value)
  }
}
