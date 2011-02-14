package org.btproject.util

object Timing {
  def measure[T](taskName:String)(exec: => T):Unit = {
    val begin = System.currentTimeMillis()
    val result = exec
    val end = System.currentTimeMillis()
    println(taskName + ": " + (end - begin).toFloat / 1000 + " sec")
    return result
  }
}
