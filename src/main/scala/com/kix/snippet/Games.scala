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
package com.kix.snippet

import lib.DateHelpers._
import lib.ImgHelpers._
import model._
import net.liftweb.http._
import S._
import SHtml._
import net.liftweb.util._
import Helpers._
import scala.xml.NodeSeq

private[snippet] object Games {

  object currentDateRange extends RequestVar(DateRange.Upcoming)

  object DateRange extends Enumeration {
    val All = Value("all")
    val Upcoming = Value("upcoming")
    val Past = Value("past")
  }

  val DateRanges = (for (d <- DateRange) yield (d.id.toString, ?(d.toString))).toList
}

import Games._

class Games {

  def upcoming5(xhtml: NodeSeq) = {
    bind("games", xhtml, "list" -> bindGames(Game upcoming 5, xhtml))
  }

  def find(xhtml: NodeSeq) = {
    def findForDateRange(dateRange: DateRange.Value) = dateRange match {
      case DateRange.All => Game.findAll
      case DateRange.Upcoming => Game.upcoming
      case DateRange.Past => Game.past
    }
    bind("games", xhtml,
         "date-range" -> select(DateRanges, Full(currentDateRange.is.id.toString), 
                                s => currentDateRange(DateRange(s.toInt)), 
                                "onchange" -> "submit();"),
         "list" -> bindGames(findForDateRange(currentDateRange.is), xhtml))
  }

  private def bindGames(games: List[Game], xhtml: NodeSeq) = {
    def bindAction(game: Game) =
      if (game.date after now)
        Tip.findByUserAndGame(User.currentUser, game) match {
          case Full(tip) => Tips editDelete tip 
          case _ => Tips create game
        }
      else
        rightImg
    games flatMap { game =>
      bind("game", chooseTemplate("template", "game", xhtml),
           "action" -> (if (User.loggedIn_?) bindAction(game) else NodeSeq.Empty),
           "date" -> format(game.date.is, locale),
           "group" -> game.group.is.toString,
           "location" -> game.location.is,
           "teams" -> game.name)
    }
  }
}
