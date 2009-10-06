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

import net.liftweb.http._
import net.liftweb.http.SHtml.fileUpload
import net.liftweb.mapper._
import net.liftweb.util._

/**
 * Helper for a persistent group.
 */
object Group extends Group with LongKeyedMetaMapper[Group]

/**
 * A persistent group.
 */
class Group extends LongKeyedMapper[Group] with IdPK {

  object name extends MappedString(this, 1)

  override def getSingleton = Group
}

/**
 * Helper for a persistent team.
 */
object Team extends Team with LongKeyedMetaMapper[Team] {

  def findByGroup(group: Group) = findAll(By(Team.group, group.id))
}

/**
 * A persistent team.
 */
class Team extends LongKeyedMapper[Team] with IdPK {

  object group extends MappedLongForeignKey(this, Group)

  object name extends MappedString(this, 100)

  override def getSingleton = Team
}
