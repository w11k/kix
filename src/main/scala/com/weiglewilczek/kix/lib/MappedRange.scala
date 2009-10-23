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
package com.weiglewilczek.kix.lib

import net.liftweb.common._
import net.liftweb.http.SHtml.select
import net.liftweb.mapper._

/**
 * Special MappedInt based on a range.
 */
class MappedRange[M <: Mapper[M]](owner: M, range: Range) extends MappedInt(owner) {

  override def toForm = Full(select(RangeMap, Full(is.toString), setFromAny(_))) 
  
  private lazy val RangeMap = range map { x => (x.toString, x.toString) }
}
