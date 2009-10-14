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
import net.liftweb.util._
import net.liftweb.util.Helpers._

/**
 * Helper for a persistent result.
 */
object Result extends Result with LongKeyedMetaMapper[Result] with SuperCRUDify[Long, Result] {

  val GoalRange = 0 to 20

  override def fieldOrder = List(game, goals1, goals2)

  override def displayName = ?("Result")

  override def showAllMenuDisplayName = ?("Results")

  override def afterSave = updatePoints _ :: super.afterSave

  private def updatePoints(result: Result) {
    def points(tip: Tip) = {
      val r = result.goals1.is - result.goals2.is
      val t = tip.goals1.is -tip.goals2.is
      (t - r) match {
        case 0 if tip.goals1.is == result.goals1.is => 5
        case 0 => 4
        case x if r * t > 0 => 3
        case _ => 0
      }
    }
    User.findAll foreach { user =>
      Tip.findByUserAndGameId(Full(user), result.game.is)  map { t => 
        points(t) match {
          case 0 => false
          case p =>
            Log info "Added %s points for tipster %s.".format(p, user.shortName)
            user.points(user.points.is + p).save
        }
      }
    }
  }
}

/**
 * A persistent result.
 */
class Result extends LongKeyedMapper[Result] with IdPK {

  object game extends MappedGame(this) {
    override def selectableGames = {
      val gamesWithResult = Result.findAll map { _.game.is }
      Game.findAll filter { game =>
        !(gamesWithResult contains game.id.is) && game.date.before(now)
      }
    }
  }

  object goals1 extends MappedRange(this, Result.GoalRange) {
    override def displayName = ?("Goals Team 1") 
  }

  object goals2 extends MappedRange(this, Result.GoalRange) {
    override def displayName = ?("Goals Team 2") 
  }

  override def getSingleton = Result
}
