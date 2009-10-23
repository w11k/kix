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

import lib.DateHelpers._

import net.liftweb.common._
import net.liftweb.http._
import S._
import SHtml._
import js.JsCmds._
import js.jquery.JqJsCmds._
import net.liftweb.util._
import Helpers._
import scala.xml.{NodeSeq, Text}

class Chat extends CometActor with CometListener {

  override def fixedRender = {
    var msg = ""
    bind("chat", chooseTemplate("chat", "input", defaultXml),
         "msg" -> text("", msg = _),
         "post" -> submit(?("Post"), () => ()))
  }

  override def render = {
    def bindMessages = chatLines flatMap { line =>
      bind("msg", chooseTemplate("msgs", "list", defaultXml),
           "content" -> toXhtml(line))
    }
    bind("msgs", defaultXml, 
         AttrBindParam("id", Text(MsgsId), "id"),
         "list" -> bindMessages)
  }

  override def lowPriority = {
    case ChatServerUpdate(lines) => {
      chatLines = lines
      val update = lines map { line => AppendHtml(MsgsId, toXhtml(line)) }
      partialUpdate(update)
    }
  }

  override def registerWith = ChatServer

  private lazy val MsgsId = uniqueId + "_msgs"

  private var chatLines = List[ChatLine]()

  private def toXhtml(line: ChatLine) =
    <li> ++
    Text(line.name + " " + formatTime(line.when, locale) + " " + line.msg) ++
    </li>
}
