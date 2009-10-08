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

import net.liftweb.http.SHtml.select
import net.liftweb.mapper._
import net.liftweb.util.Full

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

  object goals1 extends MappedRange(this, Result.GoalRange)

  object goals2 extends MappedRange(this, Result.GoalRange)

  override def getSingleton = Tip
}

/**
 * Special MappedInt based on a range.
 */
private[model] class MappedRange[M <: Mapper[M]](owner: M, range: Range) extends MappedInt(owner) {

  override def toForm = Full(select(RangeMap, Full(is.toString), setFromAny(_))) 
  
  private lazy val RangeMap = range map { x => (x.toString, x.toString) }
}
