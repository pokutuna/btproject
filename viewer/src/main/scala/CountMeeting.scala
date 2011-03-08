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
import org.btproject._
import java.sql.Timestamp

object CountMeeting extends HasDBSelector {
  def makeTable:Unit = {
    selector.db.withSession {
      try {
        MeetingCounts.ddl(DBConnector.driverType).drop
      } catch { case e => println("meetingCounts table doesn't exist")}
      MeetingCounts.ddl(DBConnector.driverType).create
    }
  }

  def start = TimestampUtil.parse(ConfigLoader.loadFile("config.xml").log_start)
  
  def count = {
    val users = UserDevice.addrIDs
    users.foreach { userID =>
      println(userID)
      selector.db.withSession { 
        val q = for { 
          rec <- for(r <- DetectRecords if r.addrID is userID) yield r
          _ <- Query.orderBy(rec.time)
        } yield rec.time ~ rec.devString

        val seq = q.list.map( i => (i._1, SerializedDevice.toIntSeq(i._2).toSet))
        val countMap = scala.collection.mutable.Map[Int,Int]()
        val countContinuedMap = scala.collection.mutable.Map[Int,Int]()
        val pool = scala.collection.mutable.Set[Int]()
        val beforePool = scala.collection.mutable.Set[Int]()
        
        var last = start
        
        def update(seq:Seq[Int]) = {
          seq.foreach (d => countMap(d) = countMap.getOrElseUpdate(d, 0) + 1)
        }

        def updateContinued(seq:Seq[Int]) = {
          seq.foreach(d => countContinuedMap(d) = countContinuedMap.getOrElseUpdate(d, 0) + 1)
        }

        seq.foreach { s =>
          if (TimestampUtil.diffSecond(s._1, last) > 60 * 11) {
            update(pool.toSeq)
            val disappersContinued = (beforePool.toSet & pool.toSet)
            updateContinued(disappersContinued.toSeq)
            pool.clear
            beforePool.clear
          }
          
          val disappers = pool -- s._2.toSet
          update(disappers.toSeq)

          val disappersContinued = (beforePool.toSet & pool.toSet) -- s._2.toSet
          updateContinued(disappersContinued.toSeq)
          
          beforePool.clear
          beforePool ++= pool
          
          pool.clear
          pool ++= s._2
          last = s._1
        }

        countMap.keys.foreach { key =>
          MeetingCounts.forInsert.insert(
            MeetingCount(userID, key, countMap(key), countContinuedMap.getOrElse(key,0)))
        }
      }
    }
  }

  def main(args: Array[String]) = {
//    makeTable
//    count
    val devices = UserDevice.addrIDs
    selector.db.withSession {
      val str = Combination(2, devices).map { comb =>
        val devA = comb.head
        val devB = comb.last
        val abMeeting = (for(r <- MeetingCounts if ((r.cliqueID is devA) && (r.target is devB))) yield r.count).firstOption.getOrElse(0)
        val baMeeting = (for(r <- MeetingCounts if ((r.cliqueID is devB) && (r.target is devA))) yield r.count).firstOption.getOrElse(0)
        val abDetect = DetectRecords.where(r => (r.devString like ("%(" + devB + ")%")) && (r.addrID is devA)).list.size
        val baDetect = DetectRecords.where(r => (r.devString like ("%(" + devA + ")%")) && (r.addrID is devB)).list.size
        val test = DetectRecords.where(r => (r.devString like ("%(10)%")) && (r.addrID is 1)).list.size
        val meeting = List(abMeeting, baMeeting).max
        val pair = SerializedDevice.mkString(comb)
        println(comb + " : " + pair)
        val pcid = PreCliques.where(_.devString is pair).first.cliqueID
        val detect = //ParentalCommunityCounts.where(_.preCliqueID is pcid).first
        List(abDetect, baDetect).max
        println(abDetect + " " + baDetect)
        val str = devA + "," + devB + "," + meeting + "," + detect + "\n"
        print(str)
        str
      }.foldLeft("")(_ + _)
      FileWrapper("count_meet_detect.csv").write(str)
    }
  }
}









