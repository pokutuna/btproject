package org.btproject.analysis

import org.btproject.util.TimestampUtilImplicits._

object Clustering {
  def main(args: Array[String]) = {
    val start = "2010/11/4 10:00:00"
    val end = "2010/11/4 11:00:00"
    val users = UserDataBuilder.timeBetween(start,end)
    val multiplicity = new UserMultiplicity(users)
    multiplicity.cluster
    println(multiplicity.clusterPool)

  }
}
