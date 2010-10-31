import scala.collection._
import java.sql._

object SQLiteTest {
  def main(args: scala.Array[String]): Unit = {

    Class.forName("org.sqlite.JDBC")
    val conn: Connection = DriverManager.getConnection("jdbc:sqlite:test.db")
    conn setAutoCommit false
    val stat = conn.createStatement()
    stat executeUpdate "drop table if exists manga;"
    stat executeUpdate "create table manga (author string, title string, volNb int);"
    val prep = conn prepareStatement "insert into manga values (?, ?, ?);"

    prep.setString(1, "Tsukasa HÃ´jÃ´")
    prep.setString(2, "City Hunter")
    prep.setInt(3, 35)
    prep.addBatch()
    prep.setString(1, "Rumiko Takahashi")
    prep.setString(2, "Ranma Â½")
    prep.setInt(3, 38)
    prep.addBatch()
    prep.setString(1, "Mitsuru Adachi")
    prep.setString(2, "Rough")
    prep.setInt(3, 12)
    prep.addBatch()

    prep.executeBatch()
    conn.commit()

    val rs: ResultSet = stat executeQuery "select * from manga;"

    while (rs.next())
    {
      println("Mangaka = " + rs.getString("author"))
      println("Title of manga = " + rs.getString("title"))
      println("Number of volumes = " + rs.getInt("volNb"))
    }
    rs.close()

    conn.close() 
  }
}
