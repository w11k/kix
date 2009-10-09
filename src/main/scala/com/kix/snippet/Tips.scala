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
package com.kix.snippet

import lib.Util.format
import model._
import net.liftweb.http._
import S.{?, locale}
import SHtml.submit
import net.liftweb.util._
import Helpers._
import scala.xml.NodeSeq

object Tips {

}

class Tips {

  def overview(xhtml: NodeSeq) = {
    def bindTips(tips: List[Tip]) = tips flatMap { tip =>
      val game = tip.game.obj
      bind("tip", chooseTemplate("template", "tip", xhtml),
           "game" -> (game map { _.name } openOr ""),
           "date" -> (game map { g => format(g.date.is, locale) } openOr ""),
           "tipster" -> (tip.user.obj map { _.shortName } openOr ""),
           "tip" -> tip.goals
      )
    }
    bind("tips", xhtml, "list" -> bindTips(Tip.findAll))
  }

  def edit(xhtml: NodeSeq) = {
    val tip = Tip.create.user(User.currentUser)
    def handleSave() {
      tip.save
      S redirectTo "."
    }
    bind("tip", xhtml,
         "game" -> tip.game.toForm,
         "tip" -> tip.toForm,
         "save" -> submit(?("Save"), handleSave)
    )
  }
}
