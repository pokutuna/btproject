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
        val o = if(detector.contains(addr)) UserNode(db.addrToName(addr)) else BTNode(db.addrToName(addr))
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

  def buildFromTimeSpanDetects(datas:Iterable[TimeSpanDetect]):Graph[Node,Edge] = {
    val edge = new IntEdgeFactory
    val graph:Graph[Node,Edge] = (new GraphBuilder).graph
    val userNodes = scala.collection.mutable.Map[String,Node]()
    datas.foreach{ d =>
      graph.addVertex(d.getNode)
      userNodes += (d.deviceName.get -> d.getNode)
    }

    import org.btproject.db._
    def addNodeAndEdge(from:Node, to:DeviceAddress, nodeGenerator:(String) => Node) = {
      val name = to.deviceName.getOrElse(to.address)
      val o:Node = userNodes.getOrElseUpdate(name,
        { val n = nodeGenerator(name); graph.addVertex(n); n})
      graph.addEdge(edge.takeEdge, from, o)
    }
  
    datas foreach { d =>
      val node = userNodes(d.deviceName.get)
      graph.addVertex(node)
      d.btDevices.foreach{ i => addNodeAndEdge(node, i, BTNode) }
      d.wifiDevices.foreach{ i => addNodeAndEdge(node, i, WifiNode) }
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
