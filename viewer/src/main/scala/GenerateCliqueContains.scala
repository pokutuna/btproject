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

object GenerateCliqueContains extends HasDBSelector {

  def makeTable:Unit = {
    selector.db.withSession {
      try {
        CliqueContains.ddl(DBConnector.driverType).drop
      } catch { case e => println("cliqueContains table doesn't exist") }
      CliqueContains.ddl(DBConnector.driverType).create
    }
  }

  def generateContains = {
    selector.db.withSession {
      val cliques = (CommunityCounts.where(_.count > 0)).list.map(_.preCliqueID)
      for (clique <- cliques;
           inner <- cliques if clique != inner) {
        if (isContain(clique, inner)) {
          CliqueContains.forInsert.insert(CliqueContain(clique, inner))
        }
      }
    }
  }

  def isContain(clique:Int, inner:Int) = {
    selector.db.withSession {
      val c = PreCliques.where(_.cliqueID is clique).first.devString
      val i = PreCliques.where(_.cliqueID is inner).first.devString
      val cset = SerializedDevice.toIntSeq(c).toSet
      val iset = SerializedDevice.toIntSeq(i).toSet
      if (iset subsetOf cset) { println(iset + " is subsetOf " + cset); true } else false
    }
  }
  
  def main(args: Array[String]) = {
    makeTable
    generateContains
  }
}
