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
 * Comet enabled chat showing all messages and rendering an input.
 */
class Chat extends CometActor with CometListener with Logging {

  override def fixedRender = {
    def handleSubmit(msg: String) {
      if (!msg.isEmpty) 
        ChatServer ! ChatMsg(User.currentUser map { _.shortName } openOr "", msg)
    }
    ajaxForm(After(100, SetValueAndFocus(InputId, "")),
             bind("chat", chooseTemplate("chat", "input", defaultXml), 
                  "input" -> text("", s => handleSubmit(s.trim), 
                                  "size" -> "64", "id" -> InputId),
                  "submit" -> submit(?("Post"), () => ())))
  }

  override def render = {
    def bindMessages = {
      lines.reverse flatMap { line =>
        bind("msg", chooseTemplate("chat", "msgs", defaultXml),
             "content" -> toXhtml(line))
      }
    }
    bind("chat", chooseTemplate("chat", "body", defaultXml), 
         AttrBindParam("id", Text(BodyId), "id"),
         "msgs" -> bindMessages)
  }

  override def lowPriority = {
    case ChatServerUpdate(newLines) => {
      log debug "ChatServerUpdate received: %s".format(newLines)
      val diff = newLines -- lines
      lines :::= diff
      partialUpdate(diff.reverse map { line => 
        AppendHtml(BodyId, toXhtml(line)) 
      })
    }
  }

  override def registerWith = ChatServer

  private lazy val BodyId = uniqueId + "_body"

  private lazy val InputId = uniqueId + "_input"

  private var lines = List[ChatLine]()

  private def toXhtml(line: ChatLine) =
    <div class="chatLine">
      <i>[{ formatTime(line.when, locale) }]</i>
      { " " }
      <b>{ line.name }</b>
      <br/>
      { line.msg }
    </div>
}
