package org.btproject.graph

import collection.JavaConversions._
import edu.uci.ics.jung.graph._
import org.btproject.graph._
import org.btproject.util._

//trait CliqueExtractor[V,E] extends Graph[V,E]{

class CliqueExtractor[V,E](graph:Graph[V,E]) { 

  def isClique(nodes:Iterable[V]):Boolean = {
    val nodeSet = nodes.toSet
    nodeSet.foreach { v =>
      val links:Set[V] = graph.getSuccessors(v).toSet
      if (!(nodeSet - v).subsetOf(links)) return false
    }
    return true
  }


  def applyCombination(size:Int, nodes:Iterable[V])(f: Iterable[V] => Boolean):Iterable[Set[V]] = {
    val res = Combination(size, nodes).filter(f(_)).map(_.toSet)
    return res
  }

  def localMaximums(minSize:Int, nodes:Iterable[V]):Iterable[Set[V]] = {
    if (minSize > nodes.size) return List()

    val buf = (minSize to nodes.size toList).flatMap{ size => 
      applyCombination(size, nodes)(isClique(_))
    }.toSet

    val cliques = buf filter{ c => !((buf - c) exists{ c subsetOf _ }) }
    cliques
  }

}

class SimpleGraph(map:Map[Int, Set[Int]]) {
  val graph:UndirectedSparseGraph[Int,Int] = new UndirectedSparseGraph[Int,Int]()
  val intFactory = new IntFactory()
  import org.btproject.model._
  val preDevs = UserDevice.addrIDs.foreach(graph.addVertex(_))
  
  for ( (k,v) <- map) {
//    graph.addVertex(k)
    v.foreach{ n => graph.addVertex(n); graph.addEdge(intFactory.take(), Seq(k, n))}
  }
  
  class IntFactory {
    var value = -1
    def take():Int = { value += 1; return value }
  }

  import scala.util.control.Exception._
  def isClique(nodes:Iterable[Int]):Boolean = {
    val nodeSet = nodes.toSet
    nodeSet.foreach { v =>
      val links = allCatch opt (graph.getSuccessors(v).toSet) getOrElse(Set())
      if (!(nodeSet - v).subsetOf(links)) return false
    }
    return true
  }
}

import org.btproject.analysis._
object CliqueExtractor {
  import org.btproject.model._
  val preDevs = UserDevice.addrIDs.toSet
  
  def extractFromTimeSpanDetects(minSize:Int, detects:Seq[TimeSpanDetect]):Set[Set[Int]] = {

    if (minSize > detects.size) return Set[Set[Int]]()
    
    val detectMap = detects.map { d =>
      (d.addrID ->
       d.detectDevices.map(_.addrID.get).filter(preDevs.contains(_)).toSet)}.toMap
    val sg = new SimpleGraph(detectMap)
    val cliques = scala.collection.mutable.Set[Set[Int]]()
  
    (minSize to sg.graph.getVertices.size toList).reverse.foreach { size =>
      Combination(size, sg.graph.getVertices).map(_.toSet)foreach{ comb => //empty target?
        comb match {
          case c if cliques.exists(c subsetOf _) =>
          case c if sg.isClique(c) =>  cliques += comb
          case _ =>
        }
      }
    }
    return cliques.toSet
  }
}

