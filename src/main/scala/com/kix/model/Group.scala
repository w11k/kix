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
import scala.xml.Text

/**
 * A group.
 */
object Group extends Enumeration {
  val A = Value("A")
  val B = Value("B")
  val C = Value("C")
  val D = Value("D")
  val E = Value("E")
  val F = Value("F")
  val G = Value("G")
  val H = Value("H")
}

/**
 * Helper for a persistent team.
 */
object Team extends Team with LongKeyedMetaMapper[Team] with CRUDify[Long, Team] {

  def findByGroup(group: Group.Value) = findAll(By(Team.group, group))
  
  override def showAllMenuName = S.?("showAllMenuName", displayName)

  override def createMenuName = S.?("createMenuName", displayName)

  override val displayName = "Team"
}

/**
 * A persistent team.
 */
class Team extends LongKeyedMapper[Team] with IdPK {

  object group extends MappedEnum(this, Group) {
    override def displayName = S.?("Group") 
  }

  object name extends MappedString(this, 100) {
    override def displayName = S.?("Name") 
  }

  override def getSingleton = Team
}
