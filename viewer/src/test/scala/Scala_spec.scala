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


import scala.xml.XML
import scala.xml.parsing._

class XMLParsingSpec extends SpecHelper {
  val xmlPath = "test_resource/config_sample.xml"
  val xml = XML.loadFile(xmlPath)

  describe("Scala XML Parser"){
    it("should scrape strings"){
      (xml \\ "log_dir").text must be ("logdata_root/log")
      (xml \\ "users" \ "name").length must be (3)
      val names = for(n <- (xml \\ "users" \ "name")) yield n.text
      names must be (List("hoge","piyo","fuga"))
    }
  }
}

class PatternMatcherSpec extends SpecHelper {

  describe("Pattern Matching"){
    val fuga = "fuga"
    val tup = ("a", "", "hoge", "piyo", fuga)
      it("(when String)") {
        val a = tup match {
          case (a, "", h, p, f) => ""
          case _ => None
        }
        a must be ("")

        val b = tup match {
          case (a, b, h, p, "fuga") => fuga
          case _ => None
        }
        b must be ("fuga")
      }

  }

}


class TypeSystemSpec extends SpecHelper {
  describe("Case class apply method"){
    it("should be overridable"){
      case class Hoge(a:String) {
        var str = ""
        def apply(a:String):Hoge = { str = a; this}
      }
      val a = Hoge("piyo")
      a.str must not be ("piyo") //umu-
    }
  }
  describe("Porimorphism"){
    it("should match SubType"){
      trait HogeTrait
      class A extends HogeTrait
      class B extends HogeTrait

      val a = List(new A, new B, new A)
      a.isInstanceOf[List[HogeTrait]] must be(true)
      
      val res = a(1) match {
        case _:B => true
        case _:HogeTrait => false
        case _ => false
      }
      res must be (true)
    }
  }
}

class SetSpec extends SpecHelper {
  import scala.collection.mutable.Set
  describe("Method example"){
    it("should check subsets"){ 
      val a = Set(1,2,3,4)
      val b = Set(2,3,4)
      b.subsetOf(a) must be (true)
      a.subsetOf(b) must be (false)
      (a-1).subsetOf(b) must be (true)
    }
  }
}
