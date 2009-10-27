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

class Chat extends CometActor with CometListener with Logging {

  override def fixedRender = {
    var msg = ""
    def handleSubmit() {
      if (!msg.isEmpty) 
        ChatServer ! ChatMsg(User.currentUser map { _.shortName } openOr "", 
                             msg)
    }
    bind("chat", chooseTemplate("chat", "input", defaultXml), 
         "input" -> text(msg, s => msg = s.trim, "size" -> "64"),
         "submit" -> submit(?("Post"), handleSubmit))
  }

  override def render = {
    val oddOrEven = OddOrEven()
    def bindMessages = {
      lines.reverse flatMap { line =>
        bind("msg", chooseTemplate("chat", "msgs", defaultXml),
             "content" -> toXhtml(line, oddOrEven.nextString))
      }
    }
    bind("chat", chooseTemplate("chat", "body", defaultXml), 
         AttrBindParam("id", Text(MsgsId), "id"),
         "msgs" -> bindMessages)
  }

  override def lowPriority = {
    case ChatServerUpdate(newLines) => {
      val oddOrEven = OddOrEven()
      log debug "ChatServerUpdate received: %s".format(newLines)
      val diff = newLines -- lines
      lines :::= diff
      partialUpdate(diff.reverse map { line => 
        AppendHtml(MsgsId, toXhtml(line, oddOrEven.nextString)) 
      })
    }
  }

  override def registerWith = ChatServer

  private lazy val MsgsId = uniqueId + "_msgs"

  private var lines = List[ChatLine]()

  private def toXhtml(line: ChatLine, oddOrEven: String) =
    <div class={ oddOrEven + " chatLine" }>
      <i>[{ formatTime(line.when, locale) }]</i>
      { " " }
      <b>{ line.name }</b>
      <br/>
      { line.msg }
    </div>
}
