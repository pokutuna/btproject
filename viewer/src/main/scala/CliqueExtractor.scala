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

import org.btproject.analysis._
object CliqueExtractor {
  def extractFromTimeSpanDetects(minSize:Int, detects:Seq[TimeSpanDetect]):Set[Set[Int]] = {
    def isClique(nodes:Set[Int], neighborMap:Map[Int,Set[Int]]):Boolean = {
      nodes.foreach { n =>
        if(!((nodes - n) subsetOf neighborMap(n))) return false
      }
      true
    }

    if (minSize > detects.size) return Set[Set[Int]]()
    val detectorIDs = detects.map(_.addrID)
    val detectMap = detects.map { d =>
      (d.addrID ->
       d.detectDevices.map(_.addrID.get).filter(detectorIDs.contains(_)).toSet)}.toMap
    val cliques = scala.collection.mutable.Set[Set[Int]]()

    println(detectorIDs)
    println(detectMap)
  
    (minSize to detects.size toList).reverse.foreach { size =>
      Combination(size, detectorIDs).map(_.toSet)foreach{ comb =>
        comb match {
          case c if cliques.exists(c subsetOf _) =>
          case c if isClique(c,detectMap) =>  cliques += comb
          case _ =>
        }
      }
    }
    return cliques.toSet
  }
}

