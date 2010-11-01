import org.scalaquery.session._
import org.scalaquery.session.Database._
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.ql.basic._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql._
import org.scalaquery.ql.basic.BasicDriver.Implicit._

object ScalaQueryTest {
  object Manga extends BasicTable[(String, String, Int)]("manga") {
    def author = column[String]("author")
    def title = column[String]("title")
    def volNb = column[Int]("volNb")
    def * = author ~ title ~ volNb
  }

  def main(args: Array[String]):Unit = { 
    val db = Database.forURL("jdbc:sqlite:test.db", driver = "org.sqlite.JDBC")
    val myQuery = for(u <- Manga) yield u.*
    db withSession {
      println(myQuery.list())
    }
  }
}
