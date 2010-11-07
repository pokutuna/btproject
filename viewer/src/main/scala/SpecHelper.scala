package org.btproject.test

import org.scalatest.Spec
import org.scalatest.matchers._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.BeforeAndAfterEach

trait SpecHelper extends Spec with ShouldMatchers with MustMatchers with BeforeAndAfterEach with BeforeAndAfterAll
