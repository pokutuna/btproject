import sbt._

class LogViewerProject(info: ProjectInfo) extends DefaultProject(info){
  val sqliteJDBC = "org.xerial" % "sqlite-jdbc" % "3.7.2"
}
