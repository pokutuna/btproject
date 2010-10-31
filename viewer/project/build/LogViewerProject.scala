import sbt._

class LogViewerProject(info: ProjectInfo) extends DefaultProject(info){
  override def compileOptions = super.compileOptions ++ compileOptions("-encoding", "utf8")
  val sqliteJDBC = "org.xerial" % "sqlite-jdbc" % "3.7.2"
  val scalaTest = "org.scalatest" % "scalatest" % "1.2"
  val orbroker = "orbroker" % "orbroker"
}
