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

import lib.SuperCRUDify
import net.liftweb.http.S.?
import net.liftweb.http.SHtml.select
import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.util.Helpers.now
import scala.xml.Text

/**
 * Helper for a persistent game.
 */
object Game extends Game with LongKeyedMetaMapper[Game] with SuperCRUDify[Long, Game] {

  override def fieldOrder = List(group, team1, team2, date, location)

  override def displayName = ?("Game")

  override def showAllMenuDisplayName = ?("Games")

  def upcoming = findAll(By_>(date, now), 
                         OrderBy(date, Ascending),
                         OrderBy(group, Ascending))

  def upcoming(n: Int) = findAll(By_>(date, now), 
                                 OrderBy(date, Ascending),
                                 OrderBy(group, Ascending),
                                 MaxRows(n))

  def past = findAll(By_<(date, now), 
                     OrderBy(date, Ascending),
                     OrderBy(group, Ascending))

  def notYetStarted_?(game: Box[Game]) =
    game map { _.date.is after now } openOr false
}

/**
 * A persistent game.
 */
class Game extends LongKeyedMapper[Game] with IdPK {

  object group extends MappedEnum(this, Group) {
    override def displayName = ?("Group")
  }

  object team1 extends MappedTeam(this, "Team 1")

  object team2 extends MappedTeam(this, "Team 2")

  object date extends MappedDateTime(this) {
    override def displayName = ?("Date") 
  }

  object location extends MappedString(this, 100) {
    override def displayName = ?("Location") 
  }

  def name = {
    def name(team: Box[Team]) = team map { _.name.is } getOrElse "?"
    name(team1.obj) + " - " + name(team2.obj)
  }

  override def getSingleton = Game
}

/**
 * A mappable game.
 */
private[model] class MappedGame[T <: Mapper[T]](mapper: T) extends MappedLongForeignKey(mapper, Game) {

  override def displayName = ?("Game")

  override def asHtml = obj map { game => Text(game.name) } openOr Text("")

  override def toForm =
    Full(select(selectableGames map { game => (game.id.is.toString, game.name) }, 
                obj map { _.id.is.toString }, 
                s => apply(s.toLong)))

  def selectableGames = Game.findAll
}
