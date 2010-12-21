package org.btproject.analysis

import org.btproject.util.TimestampUtil

object Clustering extends TimestampUtil {

  def main(args: Array[String]) = {
    val start = "2010/11/4 10:00:00"
    val end = "2010/11/4 11:00:00"
    
    val users = UserDataBuilder.timeBetween(start,end)
    (new UserMultiplicity(users)).cluster
  }
}
