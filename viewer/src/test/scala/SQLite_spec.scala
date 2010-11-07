import java.sql._
import org.btproject.test.SpecHelper

class SQLiteFileSpec extends SpecHelper { 
  var conn: Connection = null
  var stat: Statement = null

  override def beforeAll = {
    Class.forName("org.sqlite.JDBC")
    conn = DriverManager.getConnection("jdbc:sqlite:test_resource/sqlitetest.db")
    stat = conn.createStatement()
  }
  override def afterAll = {
    conn.close
    stat.close
  }

  describe("SQLiteJDBC"){
    describe("(when file)"){
      it("should locate project root"){
        val rowCount: Int = stat.executeQuery("select count(*) from manga;").getInt(1)
        rowCount should be (5)
      }
    }
  }
}








