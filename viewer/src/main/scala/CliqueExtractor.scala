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
    println(buf)
    println(cliques)
    cliques
  }

}
