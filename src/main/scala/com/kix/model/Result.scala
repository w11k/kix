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

import net.liftweb.common._
import net.liftweb.http.S.?
import net.liftweb.mapper._
import net.liftweb.util._
import Helpers._

/**
 * Helper for a persistent result.
 */
object Result extends Result with LongKeyedMetaMapper[Result] with SuperCRUDify[Long, Result] {

  val GoalRange = 0 to 20

  override def fieldOrder = List(game, goals1, goals2)

  override def displayName = ?("Result")

  override def showAllMenuDisplayName = ?("Results")

  override def afterSave = updatePointsAndTeams _ :: super.afterSave

  def findByGameId(gameId: Long) = find(By(game, gameId)) 

  private def updatePointsAndTeams(result: Result) {
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
    def inc1(m: MappedInt[Team]) { inc(m, 1) }
    def inc(m: MappedInt[Team], x: Int) { m(m.is + x) }
    User.all() foreach { user =>
      Tip.findByUserAndGameId(Full(user), result.game.is) map { tip => 
        points(tip) match {
          case 0 =>
          case x =>
            tip.points(x).save
            user.points(user.points.is + x).save
            Log info "Added %s points for tipster %s.".format(x, user.shortName)
        }
      }
    }
    Log info "XXX"
    for (game <- Game find result.game.is) {
      Log info "XXX Game" + game.name
      for (team1 <- game.team1.obj; team2 <- game.team2.obj) {
        Log info "XXX Team1" + team1.name
        Log info "XXX Team2" + team2.name
        inc(team1.posGoals, result.goals1.is)
        inc(team1.negGoals, result.goals2.is)
        inc(team2.posGoals, result.goals2.is)
        inc(team2.negGoals, result.goals1.is)
        (result.goals1.is - result.goals2.is) match {
          case 0 => 
            inc1(team1.draws)
            inc1(team2.draws)
            inc1(team1.points)
            inc1(team2.points)
          case x if (x>0) => 
            inc1(team1.wins)
            inc1(team2.losses)
            inc(team1.points, 3)
          case _ =>
            inc1(team2.wins)
            inc1(team1.losses)
            inc(team2.points, 3)
        }
        team1.save
        team2.save
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

  def goals = goals1.is + " : " + goals2.is

  override def getSingleton = Result
}
