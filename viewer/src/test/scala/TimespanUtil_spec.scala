import org.btproject.test.SpecHelper
import org.btproject.util.TimestampUtil

class TimespanUtilSpec extends SpecHelper {
  describe("TimespanUtil"){
    it("should cut off correctly"){
      val a = TimestampUtil.parse("2010/01/01 00:08:35")
      (TimestampUtil.cutOff(a,5)) must be (TimestampUtil.parse("2010/01/01 00:05:00"))
      val b = TimestampUtil.parse("2010/11/11 00:00:00")
      TimestampUtil.format(TimestampUtil.cutOff(b,5)) must be ("2010/11/11 00:00:00")
      val c = TimestampUtil.parse("2010/11/11 00:00:01")
      TimestampUtil.format(TimestampUtil.cutOff(c,5)) must be ("2010/11/11 00:00:00")
      val d = TimestampUtil.parse("2010/10/04 00:00:05")
      val e = TimestampUtil.parse("2010/10/04 00:09:00")
      TimestampUtil.cutOff(d,10) must be (TimestampUtil.cutOff(e,10))

      val f = TimestampUtil.parse("2010/10/04 00:09:30")
      TimestampUtil.diffSecond(b, c) must be (-1)
      TimestampUtil.diffSecond(f, e) must be (30)
    }
  }
}

