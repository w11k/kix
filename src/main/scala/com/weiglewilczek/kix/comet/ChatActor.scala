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
package com.weiglewilczek.kix.comet

import net.liftweb.common._
import net.liftweb.http.CometActor
import net.liftweb.util._
import Helpers._
import net.liftweb.http.js.jquery.JqJsCmds._
import scala.xml.{NodeSeq, Text}

class ChatActor extends CometActor {

  override def render = {
    def bindMessages = List("a", "b", "c") flatMap { msg =>
      bind("msg", chooseTemplate("msgs", "list", defaultXml),
           "content" -> msg)
    }
    bind("msgs", defaultXml, 
         AttrBindParam("id", Text(MsgsId), "id"),
         "list" -> bindMessages)
  }

  override def lowPriority = {
    case Tick => {
      partialUpdate(AppendHtml(MsgsId, <li>xxx</li>))
      ActorPing.schedule(this, Tick, 2 seconds)
    }
  }

  private lazy val MsgsId = uniqueId + "_elem"
      ActorPing.schedule(this, Tick, 2 seconds)
}

case object Tick
