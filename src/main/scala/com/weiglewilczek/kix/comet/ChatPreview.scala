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

import lib._
import DateHelpers._
import model._

import net.liftweb.common._
import net.liftweb.http._
import S._
import SHtml._
import js.JsCmds._
import js.jquery.JqJsCmds._
import net.liftweb.util._
import Helpers._
import scala.xml.{NodeSeq, Text}

/**
 * Comet enabled chat preview showing the latest three messages.
 */
class ChatPreview extends CometActor with CometListener with Logging {

  override def render = {
    def bindMessages = lines.reverse flatMap { line =>
      bind("msg", chooseTemplate("chat", "msgs", defaultXml),
           "content" -> toXhtml(line))
    }
    bind("chat", defaultXml, 
         AttrBindParam("id", Text(MsgsId), "id"),
         "msgs" -> bindMessages)
  }

  override def lowPriority = {
    case ChatServerUpdate(newLines) => {
      log debug "ChatServerUpdate received: %s".format(newLines)
      lines = newLines take 3
      partialUpdate(SetHtml(MsgsId, lines.reverse map { toXhtml(_) }))
    }
  }

  override def registerWith = ChatServer

  private lazy val MsgsId = uniqueId + "_msgs"

  private var lines = List[ChatLine]()

  private def toXhtml(line: ChatLine) = {
    def msg = if (line.msg.length <= 23) line.msg take 23
              else (line.msg take 20) + "..."
    <div class="chatLine">
      <b>{ line.name }</b>{ " " + msg }
    </div>
  }
}
