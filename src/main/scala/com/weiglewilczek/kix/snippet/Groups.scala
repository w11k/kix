/**
 * Copyright 2009-2010 WeigleWilczek and others.
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
package com.weiglewilczek.kix
package snippet

import lib._
import DateHelpers._
import ImgHelpers._
import model._

import net.liftweb.common._
import net.liftweb.http._
import S._
import SHtml._
import net.liftweb.mapper._
import net.liftweb.util.Helpers._
import scala.xml.{NodeSeq, Text}

object Groups {
   
   private object currentGroup extends RequestVar[Group.Value](Group.A)
   
   private val Groups4Select =
     Group.values map { group => (group.id.toString, group.toString) } toList
}

import Groups._

class Groups {

  def render(xhtml: NodeSeq) = {
    var lastPoints = 0
    var position = 1
    def bindTeams(teams: List[Team]) = {
      val oddOrEven = OddOrEven()
      teams flatMap { team =>
        if (team.points.is < lastPoints) position += 1
        lastPoints = team.points.is
        bind("team", chooseTemplate("groups", "team-list", xhtml),
             "position" -> position,
             "ensign" -> ensignImg(team.ensignUrl.is, team.name.is),
             "name" -> team.name.is,
             "points" -> team.points.is,
             "wins" -> team.wins.is,
             "draws" -> team.draws.is,
             "losses" -> team.losses.is,
             "posGoals" -> team.posGoals.is,
             "negGoals" -> team.negGoals.is,
             oddOrEven.next)
      }
    }
    def bindGames(games: List[Game]) = {
      val oddOrEven = OddOrEven()
      games flatMap { game =>
        bind("game", chooseTemplate("groups", "game-list", xhtml),
             "action" -> (if (User.loggedIn_?) Games bindAction game else NodeSeq.Empty),
             "date" -> format(game.date.is, locale),
             "location" -> game.location.is,
             "teams" -> game.name,
             "result" -> (Result goalsForGame game),
             AttrBindParam("id", Text(game.id.is.toString), "id"),
             oddOrEven.next)
      }
    }
    bind("groups", xhtml,
         "group" -> select(Groups4Select, Full(currentGroup.is.id.toString), 
                           s => currentGroup(Group(s.toInt)), 
                           "onchange" -> "submit();"),
         "team-list" -> bindTeams(Team findByGroup currentGroup.is),
         "game-list" -> bindGames(Game findByGroup currentGroup.is))
  }
}
