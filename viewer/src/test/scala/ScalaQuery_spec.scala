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
  val path = "test_resource/scalaquery"
  val dbFile = new File(path+".h2.db")
  if(dbFile.exists) dbFile.delete()
//  val db = Database.forURL("jdbc:sqlite:test_resource/scalaquery.db", driver="org.sqlite.JDBC")
//  val db = Database.forURL("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
  val db = Database.forURL("jdbc:h2:"+path, driver = "org.h2.Driver")
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

    it("should find comparison query"){
      db withSession{
        val q = Categories where(_.id > 3)
        q.list must be (List(Category(4,"four")))
        val q2 = for{ c <- Categories
                     if c.id > 2
                     if c.id < 4} yield c.*
        q2.list must be (List(Category(3,"three")))
      }
    }
    
    it("should updateable")(pending)// {
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
    it("should insert Timestamp class"){ 
      db withSession{
        Logs insertAll(
          Log("a", "2010/04/07 0:0:0"),
          Log("b", "2010/05/10 12:0:0"),
          Log("c", "2010/05/10 12:0:5"),
          Log("d", "1990/1/1 0:0:0"),
          Log("e", "2010/04/07 0:0:0")
        )

        val q = for(l <- Logs if l.str is "a") yield l.*
        q.first.str must be ("a")
        q.first.time must be (stringToTimestamp("2010/04/07 00:00:00"))
      }
    }
    
    it("should filter by Timestamp"){
      db withSession{
        val q = for(l <- Logs if l.time > stringToTimestamp("2010/04/07 0:0:0")) yield l.str
        q.list() must be (List("b","c"))
      }
    }

    it("should sort by orderBy method"){
      db withSession{
        val q = for{ 
          d <- for(l <-Logs if l.time > stringToTimestamp("1990/1/1 0:0:0 ")) yield l;
          _ <- Query.orderBy(d.time) >> Query.orderBy(d.str desc)
        } yield d.str
        q.list must be (List("e", "a", "b", "c"))
      }
    }
  }
}
