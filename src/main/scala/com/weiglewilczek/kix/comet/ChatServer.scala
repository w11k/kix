/**
 * Copyright 2009-2010 WeigleWilczek and others.
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
package com.weiglewilczek.kix
package comet

import java.util.Date
import net.liftweb.actor._
import net.liftweb.common.Loggable
import net.liftweb.http._
import S.?
import net.liftweb.util.Helpers._

/**
 * Server for Comet enabled chat.
 */
object ChatServer extends LiftActor with ListenerManager with Loggable {

  override def lowPriority = {
    case chatMsg @ ChatMsg(name, msg) if msg.trim.length > 0 =>
      logger debug "Received ChatMsg: %s".format(chatMsg)
      chatLines ::= ChatLine(name, now, msg)
      chatLines = chatLines take 5
      updateListeners()
  }

  override protected def createUpdate = ChatServerUpdate(chatLines)

  private var chatLines = List[ChatLine]()
}

case class ChatServerUpdate(lines: List[ChatLine])
case class ChatLine(name: String, when: Date, msg: String)
case class ChatMsg(name: String, msg: String)
