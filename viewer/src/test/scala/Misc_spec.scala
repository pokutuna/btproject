import org.btproject.test.SpecHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class SimpleDataFormatSpec extends SpecHelper {
  val sdf = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss")

  implicit def dateToCalendar(d:Date): Calendar = {
    val c = Calendar.getInstance()
    c.setTime(d)
    return c
  }

  implicit def calendarToDate(c:Calendar):Date = {
    return c.getTime
  }
  
  describe("(when parsing)"){
    it("should parse basic format"){
      val d = sdf.parse("2010/10/26 09:25:39")
      d.get(Calendar.DATE) should be (26)
      d.get(Calendar.HOUR_OF_DAY) should be (9)
      d.get(Calendar.SECOND) should be (39)
    }

    it("should parse without 0 at head"){
      val d = sdf.parse("2010/9/5 5:10:5")
      d.get(Calendar.YEAR) should be (2010)
      d.get(Calendar.MONTH) should be (9-1) //Jan = 0
      d.get(Calendar.DATE) should be (5)
      d.get(Calendar.HOUR) should be (5)
      d.get(Calendar.MINUTE) should be (10)
      d.get(Calendar.SECOND) should be (5)
    }

    it("should parse 24hours format"){
      val d = sdf.parse("2010/12/31 23:59:59")
      d.get(Calendar.YEAR) should be (2010)
      d.get(Calendar.MONTH) should be (12-1)
      d.get(Calendar.DATE) should be (31)
      d.get(Calendar.HOUR_OF_DAY) should be (23)
      d.get(Calendar.MINUTE) should be (59)
      d.get(Calendar.SECOND) should be (59)
    }
  }

  describe("(when formatting)"){
    it("should format with 0 at head"){
      val cal = Calendar.getInstance
      cal.set(2010,4-1,7,1,2,3)
      sdf.format(cal.getTime) should be ("2010/04/07 01:02:03")
    }
  }
}
