import org.btproject.test.SpecHelper
import org.btproject.util._
import org.scalaquery.session._
import org.scalaquery.session.Database._
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.ql._
import org.scalaquery.ql.basic._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.ql.extended._
import java.io._

class ScalaQuerySpec extends SpecHelper with TimestampUtil{
  val dbFile = new File("test_resource/scalaquery.db")
  if(dbFile.exists) dbFile.delete()
  val db = Database.forURL("jdbc:sqlite:test_resource/scalaquery.db", driver="org.sqlite.JDBC")

  override def beforeAll = { 
    db withSession {
      (Categories.ddl ++ Logs.ddl) create
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

  import java.sql.Timestamp
  case class Log(str:String, time: Timestamp)
  object Logs extends BasicTable[Log]("logs"){
    def str = column[String]("str", O NotNull)
    def time = column[Timestamp]("time", O NotNull)
    def * = str ~ time <> (Log, Log.unapply _)
  }

  describe("Insert with Date Class"){
    it("insert Timestamp class"){ 
      db withSession{
        Logs insertAll(
          Log("a", "2010/04/07 0:0:0"),
          Log("b", "2010/05/10 12:0:0"),
          Log("c", "2010/05/10 12:0:5"),
          Log("d", "1990/1/1 0:0:0")
        )

        val q = for(l <- Logs if l.str is "a") yield l.*
        q.first.str must be ("a")
        q.first.time must be (stringToTimestamp("2010/04/07 00:00:00"))
      }
    }
  }
}
