import sbt._

class LogViewerProject(info: ProjectInfo) extends DefaultProject(info){
  override def compileOptions = super.compileOptions ++ compileOptions("-encoding", "utf8")
  val sqliteJDBC = "org.xerial" % "sqlite-jdbc" % "3.7.2"
  val scalaTest = "org.scalatest" % "scalatest" % "1.2"
  val scalaQuery = "org.scalaquery" %% "scalaquery" % "0.9.0"
  //この%%は何が違うん…
  val jung = "net.sf.jung" % "jung2" % "2.0"
  
  val orbroker = "org.orbroker" % "orbroker" % "3.0.4" from "http://orbroker.googlecode.com/files/orbroker-3.0.4.jar"

}
