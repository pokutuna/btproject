import org.btproject.test.SpecHelper
import org.btproject.util.Combination

class CombinationSpec extends SpecHelper {
  describe("Combination method"){
    it("should enumerate combination"){
      val a = List(1,2,3,4,5)
      Combination(5,a).length must be (1)
      println(Combination(2,a).toList)
    }
  }
}
