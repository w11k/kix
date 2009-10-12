/**
 * Copyright 2009 WeigleWilczek and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kix.model

import lib._
import net.liftweb.http.S.?
import net.liftweb.mapper._

/**
 * Helper for a persistent result.
 */
object Result extends Result with LongKeyedMetaMapper[Result] with SuperCRUDify[Long, Result] {

  val GoalRange = 0 to 20

  override def fieldOrder = List(game, goals1, goals2)

  override def displayName = ?("Result")

  override def showAllMenuDisplayName = ?("Results")
}

/**
 * A persistent result.
 */
class Result extends LongKeyedMapper[Result] with IdPK {

  object game extends MappedGame(this)

  object goals1 extends MappedRange(this, Result.GoalRange) {
    override def displayName = ?("Goals Team 1") 
  }

  object goals2 extends MappedRange(this, Result.GoalRange) {
    override def displayName = ?("Goals Team 2") 
  }

  override def getSingleton = Result
}
