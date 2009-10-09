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

/**
 * Helper for a persistent team.
 */
object Team extends Team with LongKeyedMetaMapper[Team] with SuperCRUDify[Long, Team] {

  override def fieldOrder = List(group, name)

  override def displayName = ?("Team")

  override def showAllMenuDisplayName = ?("Teams")

  def findByGroup(group: Group.Value) = findAll(By(Team.group, group))
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
  }

  override def getSingleton = Team
}
