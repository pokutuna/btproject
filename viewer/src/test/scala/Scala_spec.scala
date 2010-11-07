import org.btproject.test.SpecHelper

class ScalaSpec extends SpecHelper { 
  describe("Implicit use for param"){
    implicit val a:Int = 1
    implicit val s:String = "a"
    
    it("should apply param implicitily"){
      def some(implicit v:Int): Int = v
      some should be (1)
    }
    
    it("should apply param with Class"){
      def some[T](implicit v:T):T = v
      some[Int] should be (1)
      some[String] should be ("a")
    }
    
    it("should find to apply func"){
      implicit def intBigger(a:Int, b:Int):Int = scala.math.max(a,b)
      implicit def strBigger(a:String, b:String):String = if(a.compareTo(b) > 0) a else b
      intBigger(1,2) should be (2)
      strBigger("a","b") should be ("b")
      
      def bigger[T](a:T, b:T)(implicit func: (T,T) => T):T = func(a,b)
      bigger(1,2) should be (intBigger(1,2))
      bigger("a","b") should be (strBigger("a","b"))
    }
  }

  describe("Implicit method adding"){ 
    //implicit Comparable
    trait Comparable[T]{ def bigger(b:T):T }
    implicit def intComparable(a:Int) = new Comparable[Int]{
      def bigger(b:Int):Int = scala.math.max(a,b)
    }
    implicit def strComparable(a:String) = new Comparable[String]{
      def bigger(b:String):String = if(a.compareTo(b) > 0) a else b
    }

    it("should find implicit method through trait"){
      1.bigger(4) should be (4)
      "z".bigger("a") should be ("z")
    }

    it("should be writable ViewBound"){
      def bigger[T](a:T, b:T)(implicit comp:T => Comparable[T]):T = a.bigger(b)
      bigger(1,2) should be (1.bigger(2))
      bigger("a","b") should be ("a".bigger("b"))

      def biggerViewBound[T<%Comparable[T]](a:T, b:T):T = a.bigger(b)
      biggerViewBound(1,2) should be (bigger(1,2))
      biggerViewBound("a","b") should be (bigger("a","b"))
    }
  }

  describe("ContextBound"){
    trait Comparator[T]{ def bigger(a:T, b:T):T }
    implicit object IntComparator extends Comparator[Int]{
      def bigger(a:Int, b:Int) = scala.math.max(a,b)
    }
    implicit object StringComparator extends Comparator[String]{
      def bigger(a:String, b:String) = if(a.compareTo(b) > 0) a else b
    }

    def bigger[T](a:T, b:T)(implicit comp:Comparator[T]) = comp.bigger(a,b)
    bigger(1,2) should be (2)
    bigger("a","b") should be ("b")

    def biggerContextBound[T:Comparator](a:T, b:T) =
      implicitly[Comparator[T]].bigger(a,b)
    biggerContextBound(4,5) should be (bigger(4,5))
    biggerContextBound("e","f") should be (bigger("e","f"))
  }

  

}
