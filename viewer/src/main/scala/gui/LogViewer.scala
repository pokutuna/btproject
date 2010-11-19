package org.btproject.gui

import scala.swing._
import org.btproject.gui
object LogViewer {
  def main(args: Array[String]) = {
    Swing.onEDT{  GUIRoot.startup(args) }
  }
}
