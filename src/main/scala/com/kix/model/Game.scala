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

import net.liftweb.http.SHtml._
import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.util.Helpers._

/**
 * Helper for a persistent game.
 */
object Game extends Game with LongKeyedMetaMapper[Game] {

  def upcoming(n: Int) = findAll(By_>(date, timeNow), 
                                 OrderBy(date, Ascending),
                                 MaxRows(n))
}

/**
 * A persistent game.
 */
class Game extends LongKeyedMapper[Game] with IdPK {

  object group extends MappedLongForeignKey(this, Group)

  object team1 extends MappedLongForeignKey(this, Team)

  object team2 extends MappedLongForeignKey(this, Team)

  object date extends MappedDateTime(this)

  object location extends MappedString(this, 100)

  def teamsToString = {
    def name(team: Box[Team]) = team map { _.name.is } getOrElse "?"
    name(team1.obj) + " - " + name(team2.obj)
  }

  override def getSingleton = Game
}

/**
 * Helper for a persistent result.
 */
object Result extends Result with LongKeyedMetaMapper[Result]

/**
 * A persistent result.
 */
class Result extends LongKeyedMapper[Result] with IdPK {

  object game extends MappedLongForeignKey(this, Game)

  object goals1 extends MappedGoal(this)

  object goals2 extends MappedGoal(this)

  override def getSingleton = Result
}

/**
 * Helper for a persistent tip.
 */
object Tip extends Tip with LongKeyedMetaMapper[Tip]

/**
 * A persistent tip.
 */
class Tip extends LongKeyedMapper[Tip] with IdPK {

  object user extends MappedLongForeignKey(this, User)

  object game extends MappedLongForeignKey(this, Game)

  object goals1 extends MappedGoal(this)

  object goals2 extends MappedGoal(this)

  override def getSingleton = Tip
}

/**
 * Special MappedInt used for goals.
 */
private[model] class MappedGoal[M <: Mapper[M]](owner: M) extends MappedInt(owner) {

  override def toForm = Full(select(Goals, Full(is.toString), setFromAny(_))) 
  
  private lazy val Goals = (0 to 20) map { x => (x.toString, x.toString) }
}
