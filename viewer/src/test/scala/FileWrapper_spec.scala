import org.btproject.test._
import org.btproject.util.FileWrapper

class FileWrapperSpec extends SpecHelper {
  describe("Wrapped java.io.File"){
    it("should expand directory"){
      val testSrcDir = FileWrapper("src/test")
      val thisFile = testSrcDir.expand().filter{ fw =>
        fw.file.getPath() == "src/test/scala/FileWrapper_spec.scala"
      }
      thisFile.head.file must be (FileWrapper("src/test/scala/FileWrapper_spec.scala").file)
//      thisFile.head must be (FileWrapper("src/test/scala/FileWrapper_spec.scala")) need compare method
    }
    it("should expand with pattern"){
      val texts = FileWrapper("test_resource").expand("""hogetext.*\.txt""".r).map(_.file.getName).toList.toSet
      texts must be (Set("hogetext_b.txt", "hogetext_a.txt"))
    }
    it("should read file"){
      val sample = FileWrapper("test_resource/piyotext.txt")
      sample.mkString must be (Some("piyo\n"))
    }
    it("should write text"){
      val path = "test_resource/out.txt"
      FileWrapper(path).write("nyanya")
      FileWrapper(path).mkString must be (Some("nyanya"))
      (new java.io.File(path)).delete()
    }
      
  }
}
