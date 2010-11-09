package org.btproject.db

import org.btproject.ConfigLoader

object DBMigration extends DBTables{
  
  def main(args:Array[String]){
    ConfigLoader.loadFile("config.xml")
    println("migrate db")
  }
}

