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

import lib.Util
import model._
import net.liftweb.http.S._
import net.liftweb.util.Helpers._
import scala.xml.NodeSeq

class Games {

  def upcoming5(xhtml: NodeSeq) = {
    def bindGames(games: List[Game]) = games flatMap { game =>
      bind("game", chooseTemplate("template", "game", xhtml),
           "tip" -> "x",
           "date" -> Util.format(game.date.is, locale),
           "group" -> game.group.is.toString,
           "location" -> game.location.is,
           "teams" -> game.teamsToString
      )
    }
    bind("games", xhtml, "list" -> bindGames(Game upcoming 5))
  }
}
