import org.btproject.test.SpecHelper
import org.scalaquery.session._
import org.scalaquery.session.Database._
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.ql._
import org.scalaquery.ql.basic._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.ql.extended._
import java.io._

class ScalaQuerySpec extends SpecHelper {
  val dbFile = new File("test_resource/scalaquery.db")
  if(dbFile.exists) dbFile.delete()
  val db = Database.forURL("jdbc:sqlite:test_resource/scalaquery.db", driver="org.sqlite.JDBC")

  override def beforeAll = { 
    db withSession {
      (Categories.ddl) create
    }
  }
  
  case class Category(id: Int, name: String)
  object Categories extends BasicTable[Category]("categories"){
    def id = column[Int]("id")
    def name = column[String]("name")
    def * = id ~ name <> (Category, Category.unapply _)
  }

  describe("(Insert & Update sample)"){
    it("could insert a single row"){
      db withSession { 
        Categories insert Category(1, "one")
        val result = for(u <- Categories if u.id is 1) yield u.name
        result.first should be ("one")
        result.list.apply(0) should be ("one") //error result.list(0)
      }
    }

    it("could insert multiple row"){
      db withSession {
        Categories insertAll(
          Category(2, "two"),
          Category(3, "three"),
          Category(4, "four")
        )
        (for(t <- Categories) yield t.id.count).first should be (4)
      }
    }

    it("could insert same collumn"){
      db withSession{ 
        Categories insert Category(1, "one");
        (for(c <- Categories if c.id is 1) yield c.id.count).first should be (2)
      }
    }

    it("could update")(pending)// {
    //   db withSession{
    //     var q = for(c <- Categories if c.id is 1) yield c.id
    //     (for(c <- Categories if c.id is 5) yield c.id.count).first should be(1) //2
    //   }
    // }

  }

  import java.sql.Date
  case class Timestamp(id: Int, time: Date) 
  object Timestamps extends ExtendedTable[Timestamp]("timestamps"){
    def id = column[Int]("id", O NotNull, O AutoInc)
    def time = column[Date]("time", O NotNull)
    def * = id ~ time <> (Timestamp, Timestamp.unapply _)
  }



  
}
