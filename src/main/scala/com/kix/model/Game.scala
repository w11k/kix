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
import net.liftweb.mapper._
import net.liftweb.util.Box
import net.liftweb.util.Helpers.timeNow

/**
 * Helper for a persistent game.
 */
object Game extends Game with LongKeyedMetaMapper[Game] with SuperCRUDify[Long, Game] {

//  override def fieldOrder = List(group, team1, team2, date, location)

  override def displayName = ?("Game")

  override def showAllMenuDisplayName = ?("Games")

  def upcoming(n: Int) = findAll(By_>(date, timeNow), 
                                 OrderBy(date, Ascending),
                                 OrderBy(group, Ascending),
                                 MaxRows(n))
}

/**
 * A persistent game.
 */
class Game extends LongKeyedMapper[Game] with IdPK {

  object group extends MappedEnum(this, Group) {
    override def displayName = ?("Group") 
  }

  object team1 extends MappedLongForeignKey(this, Team) {
    override def displayName = ?("Team 1") 
  }

  object team2 extends MappedLongForeignKey(this, Team) {
    override def displayName = ?("Team 2") 
  }

  object date extends MappedDateTime(this) {
    override def displayName = ?("Date") 
  }

  object location extends MappedString(this, 100) {
    override def displayName = ?("Location") 
  }

  def teamsToString = {
    def name(team: Box[Team]) = team map { _.name.is } getOrElse "?"
    name(team1.obj) + " - " + name(team2.obj)
  }

  override def getSingleton = Game
}
