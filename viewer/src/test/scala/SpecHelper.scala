package org.btproject

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.BeforeAndAfterAll
import org.scalatest.BeforeAndAfterEach

trait SpecHelper extends Spec with MustMatchers with BeforeAndAfterEach with BeforeAndAfterAll
