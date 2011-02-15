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

object ParentalCountCliques extends HasDBSelector {

  def makeTable:Unit = {
    selector.db.withSession {
      try {
        ParentalCommunityCounts.ddl(DBConnector.driverType).drop
      } catch { case e => println("parentalCommunityCounts table doesn't exist") }
      ParentalCommunityCounts.ddl(DBConnector.driverType).create
    }
  }

  def countParental = {
    selector.db.withSession {
      val q = for (p <- PreCliques) yield p.cliqueID
      q.list.foreach { id =>
        val parents = (for (p <- CliqueContains if p.inner is id) yield p.clique).list
        println(id + "'s parents" + " " + parents)
        
        val sum = parents.foldLeft(0) { (buf, cid) =>
          buf + CommunityCounts.where(_.preCliqueID is cid).first.count
        }
        val counts = sum + CommunityCounts.where(_.preCliqueID is id).first.count
        
        ParentalCommunityCounts.forInsert.insert(ParentalCommunityCount(id, counts))
      }
    }
  }
  
  def main(args: Array[String]) = {
    makeTable
    countParental
  }
}
