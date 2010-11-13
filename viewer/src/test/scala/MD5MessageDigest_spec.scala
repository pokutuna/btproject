import org.btproject.test.SpecHelper
import org.btproject.util._

class MD5SumSpec extends MD5MessageDigest with SpecHelper { 
  describe("MD5 checksum"){
    it("should return checksum same as md5sum(1) of Linux"){
      md5sum("test_resource/hogetext_a.txt") should be ("c59548c3c576228486a1f0037eb16a1b")
    }
    it("should generate same checksum from same inner text instead of different filename"){
      md5sum("test_resource/hogetext_a.txt") should be (md5sum("test_resource/hogetext_b.txt"))
    }
    it("should generate different checksum from different inner text"){
      md5sum("test_resource/hogetext_a.txt") should not be (md5sum("test_resource/piyotext.txt"))
    }
    it("should use as object method"){
      MD5MessageDigest("test_resource/hogetext_a.txt") should be ("c59548c3c576228486a1f0037eb16a1b")
    }
    it("should throw exception when take direcotory"){
      evaluating{ MD5MessageDigest("test_resource") } should produce [java.io.FileNotFoundException]
    }
  }
}


