package org.btproject.analysis

import scala.collection.mutable.Set

class UserMultiplicity(users:Iterable[UserData]){
  val clusterPool = Set[MultiplicityCluster]()
  users.foreach(clusterPool += MultiplicityCluster(_))

  def cluster = {
    while(clusterPool.size != 1) { step }
    println(clusterPool)
  }

  def step = {
    import scala.collection.mutable.Map
    val multiplicities = Map[Set[MultiplicityCluster], Double]()
    for{ c1 <- clusterPool
         c2 <- clusterPool if c2 != c1} multiplicities += (Set(c1,c2) -> getMultiplicity(c1,c2))

    val maxMult = multiplicities.values.max
    val mergeClusters = multiplicities.filter(_._2 == maxMult).flatMap(_._1)
    clusterPool --= mergeClusters
    clusterPool += MultiplicityCluster(mergeClusters)
  }

  def getMultiplicity(c1:MultiplicityCluster, c2:MultiplicityCluster):Double = {
    val l = for{ a <- c1.users; b <- c2.users } yield getUserMultiplicity(a,b)
    l.sum / (c1.users.size * c2.users.size).toDouble
  }

  private def getUserMultiplicity(a:UserData, b:UserData):Double = {
    (a.detects & b.detects).size / ((a.detects - b.addr) | (b.detects - a.addr)).size.toDouble
  }
}

object MultiplicityCluster {
  def apply(user:UserData):MultiplicityCluster = {
    val c = new MultiplicityCluster
    c.users += user
    return c
  }
  def apply(clusters:Iterable[MultiplicityCluster]):MultiplicityCluster = {
    new MultiplicityCluster(clusters.toSeq:_*)
  }
}
  
class MultiplicityCluster(clusters:MultiplicityCluster*) {
  val users = Set[UserData]()
  clusters.map(_.users).foreach(users ++= _)
  val innerCluster = Set[MultiplicityCluster]()
  clusters.foreach(innerCluster += _)

  override def toString:String = "MultiplicityCluster("+users.toString+")"
}
