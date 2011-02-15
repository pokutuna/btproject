import org.btproject.ConfigLoader
import org.btproject.db._
import org.btproject.util._
import org.btproject.model._
import scala.util.matching.Regex
import org.scalaquery.session._
import org.scalaquery.session.Database._
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.ql._
import org.scalaquery.ql.basic._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.BasicDriver.Implicit._
import org.scalaquery.ql.extended._

object CountCliques extends HasDBSelector {

  def makeTable:Unit = {
    selector.db.withSession {
      try {
        CommunityCounts.ddl(DBConnector.driverType).drop
      } catch { case e => println("communityCounts table doesn't exist") }
      CommunityCounts.ddl(DBConnector.driverType).create
    }
  }

  def countCommunities = {
    selector.db.withSession {
      val q = for (p <- PreCliques) yield p.cliqueID
      q.list.foreach { id =>
        val count = Query(CommunityRecords.where(_.preCliqueID is id).count)
        val cc = CommunityCount(id, count.first)
        println(cc)
        CommunityCounts.forInsert.insert(cc)
      }
    }
  }
  
  def main(args: Array[String]) = {
    makeTable
    countCommunities
  }
}
