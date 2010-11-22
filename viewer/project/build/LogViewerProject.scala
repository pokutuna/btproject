import sbt._

class LogViewerProject(info: ProjectInfo) extends DefaultProject(info){
  override def compileOptions = super.compileOptions ++
    compileOptions("-encoding", "utf8")

  //libraries
  val scalaTest = "org.scalatest" % "scalatest" % "1.2"
  val h2DataBaseEngine = "com.h2database" % "h2" % "1.2.144"
  val scalaQuery = "org.scalaquery" % "scalaquery" % "0.9.0" from "http://scala-tools.org/repo-releases/org/scalaquery/scalaquery_2.8.0/0.9.0/scalaquery_2.8.0-0.9.0.jar"
  //val scalaQuery = "org.scalaquery" % "scalaquery" % "0.9.0"
  val scalaSwing = "org.scala-lang" % "scala-swing" % "2.8.1"
  
  import Process._
  lazy val syncLogData = task { "sh ./logdata_root/sync.sh" !; None}
  lazy val downloadJung = task { "sh ./lib/download_jung2.sh" !; None}

  override def mainClass = Some("org.btproject.gui.LogViewer")
}
