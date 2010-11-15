package org.btproject.util

import scala.io.Source
import scala.util.matching.Regex
import java.io.{ File => JFile, _ }

object FileWrapper {
  def apply(path:String):FileWrapper = new FileWrapper(new JFile(path))

  implicit def jFileToFileWrapper(file:JFile): FileWrapper = new FileWrapper(file)
  implicit def fileWrapperToJFile(file:FileWrapper): JFile = file.file
}

class FileWrapper(val file:JFile) {
  def expand(pattern:Regex = ".*".r): Iterable[FileWrapper] = {
    def expandThis(f:JFile): Iterable[FileWrapper] = {
      if(f.isDirectory){
        f.listFiles.flatMap(expandThis _)
      } else {
        if(pattern.findFirstIn(f.getName) != None) List(new FileWrapper(f)) else List()
      }
    }
    expandThis(this.file).toList
  }

  def getLines: Iterator[String] = Source.fromFile(this.file).getLines

  def foreachLine(proc:String => Unit): Unit = {
    val src = Source.fromFile(this.file)
    try {
      for(line <- src.getLines) proc(line)
    } finally {
      src.close()
    }
  }

  def mkString: Option[String] = {
    var dest:Option[String] = None
    val src = Source.fromFile(this.file)
    try {
      dest = Some(src.mkString)
    } finally {
      src.close()
    }
    return dest
  }
  
  def write(text:String): Unit = {
    val fw = new FileWriter(this.file)
    try {
      fw.write(text)
      fw.flush()
    } finally {
      fw.close()
    }
  }

  def md5sum: String = {
    MD5MessageDigest(this.file.getPath)
  }

  override def toString: String = {
    "FileWrapper(%s)".format(this.file.getPath)
  }
}
