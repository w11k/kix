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
package com.weiglewilczek.kix.model

import lib._

import net.liftweb.http.S.?
import net.liftweb.http.SHtml.select
import net.liftweb.mapper._
import net.liftweb.common._
import scala.xml.Text

/**
 * Helper for a persistent team.
 */
object Team extends Team with LongKeyedMetaMapper[Team] with SuperCRUDify[Long, Team] {

  override def fieldOrder = List(group, name, ensignUrl)

  override def displayName = ?("Team")

  override def showAllMenuDisplayName = ?("Teams")

  def findByGroup(group: Group.Value) = findAll(By(Team.group, group),
                                                OrderBy(Team.points, Descending))
}

/**
 * A persistent team.
 */
class Team extends LongKeyedMapper[Team] with IdPK {

  object group extends MappedEnum(this, Group) {
    override def displayName = ?("Group") 
  }

  object name extends MappedString(this, 100) {
    override def displayName = ?("Name") 
    override def is = ?(super.is)
  }

  object ensignUrl extends MappedString(this, 256) {
    override def displayName = ?("Ensign URL") 
  }

  object wins extends MappedInt(this)

  object draws extends MappedInt(this)

  object losses extends MappedInt(this)

  object points extends MappedInt(this)

  object posGoals extends MappedInt(this)

  object negGoals extends MappedInt(this)

  override def getSingleton = Team
}

/**
 * A mappable team.
 */
private[model] class MappedTeam(game: Game, dispName: String) extends MappedLongForeignKey(game, Team) {

  override def displayName = ?(dispName)

  override def asHtml = obj map { team => Text(team.name.is) } openOr Text("")

  override def toForm =
    Full(select(Team.findAll map { team => (team.id.is.toString, team.name.is) }, 
                obj map { _.id.toString }, 
                s => apply(s.toLong)))
}
