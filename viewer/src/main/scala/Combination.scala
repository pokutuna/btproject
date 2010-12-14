package org.btproject.util

object Combination {

  def apply[A](n:Int, itr:Iterable[A]):Stream[List[A]] = {
    def f(ls:List[A], rest:Stream[A], n:Int):Stream[List[A]] = {
      if (n == 0) Stream(ls)
      else if (rest.isEmpty) Stream.empty
      else f(rest.head :: ls, rest.tail, n - 1) #::: f(ls, rest.tail, n)
    }
    f(Nil, itr.toStream, n)
  }
}
