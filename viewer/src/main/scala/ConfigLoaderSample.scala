package org.btproject

object ConfigLoaderSample {
  def main(args: Array[String]) = {
    val c = ConfigLoader.loadFile("config.xml")
    println(c)
    c.users.foreach { u =>
      println(u.name)
      println(u.deviceName)
      println(u.path)
    }

    println("Students:")
    c.users.filter(_.isStudent).foreach(u=>println(u.name))

    println("B4s:")
    c.users.filter(_.grade == "B4").foreach(u=>println(u.name))
    
    println("Smokers:")
    c.users.filter(_.isSmoker).foreach(u=>println(u.name))

    println("NightPersons:")
    c.users.filter(_.isNightPerson).foreach(u=>println(u.name))
  }
}
