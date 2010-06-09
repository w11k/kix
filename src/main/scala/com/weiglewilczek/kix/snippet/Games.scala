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
import net.liftweb.util.Helpers._
import scala.xml.{NodeSeq, Text}

object Games {

  object currentDateRange extends RequestVar(DateRange.Upcoming)

  object DateRange extends Enumeration {
    val All = Value("all")
    val Upcoming = Value("upcoming")
    val Past = Value("past")
  }

  def dateRanges = (for (d <- DateRange.values) yield (d.id.toString, ?(d.toString))).toList

  def bindAction(game: Game): NodeSeq =
    (Tip.findByUserAndGameId(User.currentUser, game.id.is), game.date after now) match {
      case (Full(tip), true)  => Tips.editDelete(tip, game, bindAction _)
      case (Full(tip), false) => Tips points tip
      case (Empty, true)      => Tips create game
      case _                  => NodeSeq.Empty
    }

  def bindGoals(game: Game): String =
    Tip.findByUserAndGameId(User.currentUser, game.id.is) match {
      case Full(tip)  => Tips goals tip
      case _          => ""
    }

  def bindResult(game: Game): String =
    Result goalsForGame game
}

import Games._

class Games {

  def upcoming5(xhtml: NodeSeq) =
    bind("games", xhtml, "list" -> bindGames(Game upcoming 5, xhtml))

  def forDateRange(xhtml: NodeSeq) = {
    def findForDateRange(dateRange: DateRange.Value) = dateRange match {
      case DateRange.All => Game.all
      case DateRange.Upcoming => Game.upcoming
      case DateRange.Past => Game.past
    }
    bind("games", xhtml,
         "date-range" -> select(dateRanges, Full(currentDateRange.is.id.toString), 
                                s => currentDateRange(DateRange(s.toInt)), 
                                "onchange" -> "submit();"),
         "list" -> bindGames(findForDateRange(currentDateRange.is), xhtml))
  }

  private def bindGames(games: List[Game], xhtml: NodeSeq) = {
    val oddOrEven = OddOrEven()
    games flatMap { game =>
      bind("game", chooseTemplate("games", "list", xhtml),
           "action" -> (if (User.loggedIn_?) bindAction(game) else NodeSeq.Empty),
           "date" -> format(game.date.is, locale),
           "group" -> game.group.is.toString,
           "location" -> game.location.is,
           "teams" -> game.name,
           "tip" -> bindGoals(game),
           "result" -> bindResult(game),
           AttrBindParam("id", Text(game.id.is.toString), "id"),
           oddOrEven.next)
    }
  }
}
