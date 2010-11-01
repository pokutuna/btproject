import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.BeforeAndAfterEach
import org.scalatest.BeforeAndAfterAll
import java.sql._

class SQLiteSpec extends Spec with MustMatchers with BeforeAndAfterEach with BeforeAndAfterAll {
  
  var conn: Connection = null
  var stat: Statement = null
  override def beforeAll = {
    Class.forName("org.sqlite.JDBC")
    conn = DriverManager.getConnection("jdbc:sqlite:test.db")
    stat = conn.createStatement()
  }
  override def afterAll = {
    conn.close
  }
  
  describe("SQLiteJDBC"){
    describe("(when file)"){
      it("should locate project root"){
        val rowCount: Int = stat.executeQuery("select count(*) from manga;").getInt(1)
        rowCount must be (5)
      }
    }
  }
  
}
