package org.btproject.util

import org.btproject.db._
import org.btproject.model._
import java.util.Date

object DBBenchmark {
  var count = 0
  def establishSession() = {
    val session = dbs.db.createSession()
    count += 1
    session.close()
  }
  val dbs = DBGraphSelector.getSelector
  
  def main(args: Array[String]) = {

    val start = (new Date).getTime
    for(i <- 1 to 10000) {
      establishSession()
    }

    val diff = (new Date).getTime - start
    println(diff.toFloat / 1000)
  }
}

