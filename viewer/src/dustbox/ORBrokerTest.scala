import java.sql._
import org.orbroker._
import org.orbroker.pimp._

object ORBrokerTest {
  def main(args: scala.Array[String]): Unit = {
    val ds = new SQLiteDataSource("jdbc:sqlite:test.db")
    val builder = new BrokerBuilder(ds)
    builder.register('countRows, "select count (*) from manga")
    val broker = builder.build
    val count = broker.readOnly(){ session => 
      session.selectOne('countRows).get
    }
    printf("%d rows",count)
  }
}
  
import java.sql._
import javax.sql.DataSource
import java.io._
class SQLiteDataSource(val uri:String) extends DataSource {
  Class forName "org.sqlite.JDBC"

  def getConnection: Connection = 
    DriverManager.getConnection(this.uri)

  def getConnection(u:String, p: String): Connection = 
    getConnection

  def getLoginTimeout: Int = return 0
  def setLoginTimeout(s: Int): Unit = {}
  def getLogWriter: PrintWriter = null
  def setLogWriter(out: PrintWriter) = {}
  def isWrapperFor(iface: Class[_]): Boolean = false
  def unwrap[T](iface: Class[T]): T = throw new NoSuchElementException
}
