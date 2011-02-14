package org.btproject.db

trait HasDBSelector {
  lazy val selector:newDBSelector = newDBSelector.getSelector
}
