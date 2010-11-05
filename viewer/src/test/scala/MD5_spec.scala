import java.security.MessageDigest
import java.security.DigestInputStream
import java.io.BufferedInputStream
import java.io.FileInputStream
import org.btproject.test.SpecHelper

class MD5SumSpec extends SpecHelper { 

  def md5sum(filename: String): String = {
    val stream = new FileInputStream(filename)
    try {
      val buf = Stream.continually( stream.read ).takeWhile( -1 != _ ).map{ _.byteValue }.toArray
      val md5 = MessageDigest.getInstance("MD5")
      md5.update(buf)
      return md5.digest().map("%02x".format(_)).mkString
    } finally {
      stream.close()
    }
  }

  describe("md5sum checksum"){
    it("should return checksum same as md5sum(1) of Linux"){
      md5sum("test_resource/hogetext_a.txt") must be ("c59548c3c576228486a1f0037eb16a1b")
    }
    it("should generate same checksum from same inner text instead of different filename"){
      md5sum("test_resource/hogetext_a.txt") must be (md5sum("test_resource/hogetext_b.txt"))
    }
    it("should generate different checksum from different inner text"){
      md5sum("test_resource/hogetext_a.txt") must not be (md5sum("test_resource/piyotext.txt"))
    }
  }
}

  
