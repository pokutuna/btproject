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

object MigrateCliques extends HasDBSelector{

  def makeTables:Unit = {
    NewDBTables.communityTableList.foreach{ t =>
      selector.db.withSession {
        try { 
          t.ddl(DBConnector.driverType).drop
        } catch { case e => println("table doesn't exist: " + t.tableName) }
        t.ddl(DBConnector.driverType).create
      }
    }
  }

  import org.btproject._
  def initPreCliques(users:Seq[User]):Unit = {
    val dev = SerializedDevice.format(users.map(_.deviceAddress))
    val selector = newDBSelector.getSelector
    val seq = dev.toIntSeq
    val comvs = (1 to seq.size toList).flatMap{ size =>
      Combination(size, seq).toSeq.map{ SerializedDevice.mkString(_)}
    }
    selector.db.withSession {
      comvs.foreach( c => PreCliques.devString.insert(c))
    }
  }

  import java.sql.Timestamp
  import org.btproject.util.TimestampUtil._
  import org.btproject.analysis._
  def insertCommunity(times:Seq[Timestamp], widthMinutes:Int, incMinutes:Int) = {
    var checked = new Timestamp(0)
//    val schedules = new scala.collection.mutable.ListBuffer[(Timestamp, Timestamp, Int)]()
    
    times.foreach { t =>
      if (checked.getTime < t.getTime) {
        val begin = t
        val end = minutesLater(begin, widthMinutes - 1)
        checked = minutesLater(begin, (incMinutes - 1))
        println(begin + " ~ " + end) // + ": checked: " + checked)
        val communities = CommunityExtractor.timeBetween(begin, end, widthMinutes)
//        println(communities)
        communities.foreach(selector.addCommunityRecord(_))
//        schedules += ((begin, end, widthMinutes))
      }
    }
//    new TimeManagerActor(3, schedules.toList)
  }

  def timeWindowCliques(cl:ConfigLoader):Unit = {
    val start = TimestampUtil.parse(cl.log_start)
    val end = TimestampUtil.parse(cl.log_end)
    
    val times = selector.db.withSession {
      val q = for {
        d <- for(l <-DetectRecords if l.time.between(start,end)) yield l
        _ <- Query.orderBy(d.time)
      } yield d.time
      q.list
    }
    insertCommunity(times.distinct, 20, 10)
  }
  
  def main(args: Array[String]) = {
    println("create clique database? [Y/n] ")
    if (readLine() != "Y") exit

    val cl = ConfigLoader.loadFile("config.xml")
    makeTables
    initPreCliques(cl.users)
    timeWindowCliques(cl)
  }
}
