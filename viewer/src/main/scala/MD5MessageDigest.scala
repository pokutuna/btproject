package org.btproject.util

import java.security.MessageDigest
import java.security.DigestInputStream
import java.io.BufferedInputStream
import java.io.FileInputStream

object MD5MessageDigest extends MD5MessageDigest {
  def apply(path:String) = md5sum(path)
}

trait MD5MessageDigest {
  def md5sum(path: String): String = {
    val stream = new FileInputStream(path)
    try {
      val buf = Stream.continually( stream.read ).takeWhile( -1 != _ ).map{ _.byteValue }.toArray
      val md5 = java.security.MessageDigest.getInstance("MD5")
      md5.update(buf)
      return md5.digest().map("%02x".format(_)).mkString
    } finally {
      stream.close()
    }
  }
}

